package com.xc.apex.nre.lib_payment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pos.sdk.accessory.POIGeneralAPI;
import com.pos.sdk.emvcore.IPosEmvCoreListener;
import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.PosEmvErrorCode;
import com.pos.sdk.printer.POIPrinterManager;
import com.pos.sdk.security.POIHsmManage;
import com.pos.sdk.security.PedKcvInfo;
import com.pos.sdk.security.PedKeyInfo;
import com.xc.apex.nre.lib_payment.data.InjectorUtils;
import com.xc.apex.nre.lib_payment.data.TransactionData;
import com.xc.apex.nre.lib_payment.data.TransactionRepository;
import com.xc.apex.nre.lib_payment.device.DeviceConfig;
import com.xc.apex.nre.lib_payment.emv.EmvConfig;
import com.xc.apex.nre.lib_payment.emv.utils.EmvCard;
import com.xc.apex.nre.lib_payment.utils.AppExecutors;
import com.xc.apex.nre.lib_payment.utils.AppUtils;
import com.xc.apex.nre.lib_payment.utils.AudioBeep;
import com.xc.apex.nre.lib_payment.utils.GlobalData;
import com.xc.apex.nre.lib_payment.utils.tlv.BerTag;
import com.xc.apex.nre.lib_payment.utils.tlv.BerTlv;
import com.xc.apex.nre.lib_payment.utils.tlv.BerTlvBuilder;
import com.xc.apex.nre.lib_payment.utils.tlv.BerTlvParser;
import com.xc.apex.nre.lib_payment.utils.tlv.BerTlvs;
import com.xc.apex.nre.lib_payment.utils.tlv.HexUtil;
import com.xc.apex.nre.lib_payment.view.PasswordDialog;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 卡支付相关
 */
public class PaymentManager {
    private static final String TAG = "PaymentManager";

    private static volatile PaymentManager instance;
    private Context context;
    private Activity actContext; // 用于显示支付密码弹窗-待优化
    private boolean isSdkInitSuccess = false; // 金融SDK初始化成功
    private InitPaymentCallbackListener initPaymentCallbackListener;// 金融SDK初始化结果的回调
    private PaymentResultListener paymentResultListener;// 支付结果回调

    // 金融相关变量--->start
    private final int MSG_SUCCESS = 1;
    private final int MSG_FAILED = 2;

    private long curAmountVal;
    private int transType;
    private boolean isFallBack;
    private int cardType;
    private POIEmvCoreManager emvCoreManager;
    private POIEmvCoreListener emvCoreListener;
    private TransactionData transData;
    private TransactionRepository transRepository;
    AudioBeep mBeeper = null;
    private boolean isBeep = false;
    private boolean isTransTest = false;
    private PasswordDialog passwordDialog;
    // 金融相关变量--->end

    public static PaymentManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PaymentManager.class) {
                if (instance == null) {
                    instance = new PaymentManager(context);
                }
            }
        }
        return instance;
    }

    private PaymentManager(Context context) {
        this.context = context;
    }

    /**
     * 初始化金融SDK
     *
     * @param initCallback
     */
    public void initPaymentSdk(InitPaymentCallbackListener initCallback) {
        this.initPaymentCallbackListener = initCallback;
        initTrans();
    }

    /**
     * 金融SDK是否可用
     *
     * @return
     */
    public boolean isPaymentSdkAvailable() {
        return isSdkInitSuccess;
    }

    /**
     * 开始支付
     *
     * @param amount   金额
     * @param listener 结果回调
     */
    public void startToTrans(@NonNull String amount, boolean showPasswordDialog, Activity context, PaymentResultListener listener) {
        this.paymentResultListener = listener;

        if (amount != null && !amount.trim().isEmpty() && isPositiveNumeric(amount)) {
            Double amountD = Double.valueOf(amount);
            DecimalFormat df = new DecimalFormat("#.00");
            String amountRes = df.format(amountD);
            curAmountVal = Double.valueOf(Double.valueOf(amountRes) * 100).longValue();
            Log.d(TAG, "startToTrans:: curAmountVal = " + curAmountVal);

            isTransTest = !showPasswordDialog;
            actContext = context;
            onTransStart();
        } else {
            curAmountVal = 0;
            Log.e(TAG, "The amount is illegal: amount = " + (amount == null ? "null" : amount));
        }
    }

    // 停止交易
    public void stopToTrans() {
        Log.d(TAG, "stopToTrans+");
        emvCoreManager.stopTransaction();
    }

    // 关闭交易
    public void shutdownTrans() {
        if (passwordDialog != null) {
            passwordDialog.closePasswordDialog();
        }
        handler.removeCallbacksAndMessages(null);
        if (emvCoreManager != null) {
            emvCoreManager.stopTransaction();
        }
        paymentResultListener = null;
        curAmountVal = 0;
        actContext = null;
    }

    public void playSuccessBeep() {
        POIGeneralAPI.getDefault().setBeep(true, 1500, 200);
    }

    private boolean isPositiveNumeric(String str) {
        Pattern pattern = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
        return pattern.matcher(str).matches();
    }

    public interface InitPaymentCallbackListener {
        void onInitResult(boolean isSuccess, Exception e);
    }

    public interface PaymentResultListener {
        void onPaymentSuccess(boolean paySuccess, String msg, int resultCode);
    }

    private void paymentResultCallback(boolean isSuccess, String msg, int resultCode) {
        if (this.paymentResultListener != null) {
            paymentResultListener.onPaymentSuccess(isSuccess, msg, resultCode);
        }
    }

    // --------------------------金融 SDK--------------------------
    // Step1：初始化金融库--->start
    private void initTrans() {
        isSdkInitSuccess = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    if (initPaymentCallbackListener != null) {
                        initPaymentCallbackListener.onInitResult(false, e);
                    }
                    e.printStackTrace();
                    return;
                }
                doInitTrans();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    if (initPaymentCallbackListener != null) {
                        initPaymentCallbackListener.onInitResult(false, e);
                    }
                    e.printStackTrace();
                    return;
                }
                if (initPaymentCallbackListener != null) {
                    initPaymentCallbackListener.onInitResult(true, null);
                    isSdkInitSuccess = true;
                    initPaymentAttr();
                }
            }
        }).start();
    }

    private void initPaymentAttr() {
        transType = GlobalData.getTransType();
        emvCoreManager = POIEmvCoreManager.getDefault();
        emvCoreListener = new POIEmvCoreListener();
        transData = new TransactionData();
        transRepository = InjectorUtils.getTransRepository(context);
        mBeeper = new AudioBeep(context);
        this.passwordDialog = new PasswordDialog();
    }

    private void doInitTrans() {
        try {
            onInitConfig();
            int result = 0;
            result += writePinKey(DeviceConfig.PIN_INDEX, DeviceConfig.PIN_DATA);
            result += writeDukptKey(DeviceConfig.DUKPT_INDEX, DeviceConfig.DUKPT_IPEK, DeviceConfig.DUKPT_KSN);
//        if (result == 0) {
//            Toast.makeText(getApplicationContext(), "succeed", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
//        }
            EmvConfig.loadTerminal();
            EmvConfig.loadAid();
            EmvConfig.loadCapk();
            EmvConfig.loadExceptionFile();
            EmvConfig.loadRevocationIPK();

            EmvConfig.loadVisa();
            EmvConfig.loadUnionPay();
            EmvConfig.loadMasterCard();
            EmvConfig.loadDiscover();
            EmvConfig.loadAmex();
            EmvConfig.loadMir();
            EmvConfig.loadVisaDRL();
            EmvConfig.loadAmexDRL();
            EmvConfig.loadService();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private void onInitConfig() {
        AppUtils.init(context);
    }

    private int writePinKey(int keyIndex, String keyData) {
        PedKeyInfo pedKeyInfo = new PedKeyInfo(0, 0, POIHsmManage.PED_TPK, keyIndex, 0, 16, HexUtil.parseHex(keyData));
        return POIHsmManage.getDefault().PedWriteKey(pedKeyInfo, new PedKcvInfo(0, new byte[5]));
    }

    private int writeDukptKey(int keyIndex, String keyData, String ksnData) {
        PedKcvInfo kcvInfo = new PedKcvInfo(0, new byte[5]);
        return POIHsmManage.getDefault().PedWriteTIK(keyIndex, 0, 16, HexUtil.parseHex(keyData), HexUtil.parseHex(ksnData), kcvInfo);
    }
    // 初始化金融库--->end

    // Step2：付款 & 回调--->start
    private void onTransStart() {
        long amountOther = 0;
        transData = null;
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_TYPE, transType);
            bundle.putLong(POIEmvCoreManager.EmvTransDataConstraints.TRANS_AMOUNT, curAmountVal);
            bundle.putLong(POIEmvCoreManager.EmvTransDataConstraints.TRANS_AMOUNT_OTHER, amountOther);

            if (isFallBack) {
                bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_MODE, POIEmvCoreManager.DEVICE_MAGSTRIPE);
                bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.TRANS_FALLBACK, true);
            } else {
                int mode = 0;
                if (GlobalData.isSupportContact()) {
                    mode |= POIEmvCoreManager.DEVICE_CONTACT;
                }
                if (GlobalData.isSupportContactless()) {
                    mode |= POIEmvCoreManager.DEVICE_CONTACTLESS;
                }
                if (GlobalData.isSupportMagstripe()) {
                    mode |= POIEmvCoreManager.DEVICE_MAGSTRIPE;
                }
                bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_MODE, mode);
                bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.APPLE_VAS, GlobalData.isSupportAppleVas());
            }

            bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_TIMEOUT, 60);
            bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_CONTACT, !isTransTest);  //normal is true
            bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_MAGSTRIPE, !isTransTest);  //normal is true
            bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.TRANS_FALLBACK, true);

            transData = new TransactionData();
            transData.setTransType(transType);
            transData.setTransAmount((double) curAmountVal);
            transData.setTransAmountOther((double) amountOther);
            transData.setTransResult(PosEmvErrorCode.EMV_OTHER_ERROR);
            bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.USE_CARD_READ_SUCCESS, true);
            int result = emvCoreManager.startTransaction(bundle, emvCoreListener);

            isFallBack = false;

            if (PosEmvErrorCode.EXCEPTION_ERROR == result) {
                Log.e(TAG, "startTransaction exception error");
                paymentResultCallback(false, "startTransaction exception error", result);
                onTransEnd(result);
            } else if (PosEmvErrorCode.EMV_ENCRYPT_ERROR == result) {
                Log.e(TAG, "startTransaction encrypt error");
                paymentResultCallback(false, "startTransaction encrypt error", result);
                onTransEnd(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onTransEnd(int result) {

        final POIPrinterManager printerManager =
                new POIPrinterManager(context.getApplicationContext());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                paymentResultCallback(false, "Payment failed.", result);
            }
        }, 300);
    }

    void onSuccess() {
        handler.sendEmptyMessage(MSG_SUCCESS);
    }

    void onFailed() {
        handler.sendEmptyMessage(MSG_FAILED);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    POIGeneralAPI.getDefault().setBeep(true, 1500, 200);
                    isBeep = true;
                    break;
                case MSG_FAILED:
                    POIGeneralAPI.getDefault().setBeep(true, 200, 300);
                default:
                    break;
            }
        }
    };

    public class POIEmvCoreListener extends IPosEmvCoreListener.Stub {

        private String TAG = "PosEmvCoreListener";

        @Override
        public void onEmvProcess(final int type, Bundle bundle) {
            Log.d(TAG, "onEmvProcess: ");
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    cardType = type;
                    switch (type) {
                        case POIEmvCoreManager.DEVICE_CONTACT:
                            Log.e(TAG, "Contact Card Trans");
                            break;
                        case POIEmvCoreManager.DEVICE_CONTACTLESS:
                            Log.e(TAG, "Contactless Card Trans");
                            break;
                        case POIEmvCoreManager.DEVICE_MAGSTRIPE:
                            Log.e(TAG, "Magstripe Card Trans");
                            break;
                        case PosEmvErrorCode.EMV_MULTI_CONTACTLESS:
//                            tvMessage1.setText("Multiple cards\nPresent a single card");
                            onFailed();
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onTransStart();
                                }
                            }, 2000);
                            return;
                        default:
                            break;
                    }
//                    tvMessage2.setText("Processing");
                }
            });
        }

        @Override
        public void onSelectApplication(final List<String> list, boolean isFirstSelect) {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    String[] names = list.toArray(new String[0]);
                    emvCoreManager.onSetSelectResponse(0);

                }
            });
        }

        @Override
        public void onConfirmCardInfo(int mode, Bundle bundle) {
            Log.d(TAG, "onConfirmCardInfo: ");
            Bundle outBundle = new Bundle();
            if (mode == POIEmvCoreManager.CMD_AMOUNT_CONFIG) {
                outBundle.putString(POIEmvCoreManager.EmvCardInfoConstraints.OUT_AMOUNT, "11");
                outBundle.putString(POIEmvCoreManager.EmvCardInfoConstraints.OUT_AMOUNT_OTHER, "22");
            } else if (mode == POIEmvCoreManager.CMD_TRY_OTHER_APPLICATION) {
                outBundle.putBoolean(POIEmvCoreManager.EmvCardInfoConstraints.OUT_CONFIRM, true);
            } else if (mode == POIEmvCoreManager.CMD_ISSUER_REFERRAL) {
                outBundle.putBoolean(POIEmvCoreManager.EmvCardInfoConstraints.OUT_CONFIRM, true);
            } else if (mode == POIEmvCoreManager.CMD_CARD_READ_SUCCESS) {
                Log.d(TAG, "onConfirmCardInfo: CMD_CARD_READ_SUCCESS");
//                return;
            }
            emvCoreManager.onSetCardInfoResponse(outBundle);
        }

        @Override
        public void onKernelType(int type) {
            transData.setCardType(type);
        }

        @Override
        public void onSecondTapCard() {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Second Tap Card");
                }
            });
        }

        @Override
        public void onRequestInputPin(final Bundle bundle) {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    boolean isIcSlot = cardType == POIEmvCoreManager.DEVICE_CONTACT;
                    //isTransTest = true;  // no pin
                    if (isTransTest) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(POIEmvCoreManager.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, POIEmvCoreManager.EmvPinConstraints.VERIFY_SUCCESS);
                        bundle.putInt(POIEmvCoreManager.EmvPinConstraints.OUT_PIN_TRY_COUNTER, 0);
                        bundle.putByteArray(POIEmvCoreManager.EmvPinConstraints.OUT_PIN_BLOCK, HexUtil.parseHex("0102030405060708"));
                        POIEmvCoreManager.getDefault().onSetPinResponse(bundle);
                    } else {
                        if (actContext != null) {
                            if (passwordDialog == null) {
                                passwordDialog = new PasswordDialog();
                            }
                            passwordDialog.createPasswordDialog(actContext, isIcSlot, bundle, DeviceConfig.PIN_INDEX);
                            passwordDialog.showDialog();
                        }
                    }
                }
            });
        }

        @Override
        public void onRequestOnlineProcess(final Bundle bundle) {
            onSuccess();
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "  Authorizing,Please Wait  ");
                }
            });

            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    byte[] data;
                    int vasResult;
                    byte[] vasData;
                    byte[] vasMerchant;
                    int encryptResult;
                    byte[] encryptData;

                    vasResult = bundle.getInt(POIEmvCoreManager.EmvOnlineConstraints.APPLE_RESULT, PosEmvErrorCode.APPLE_VAS_UNTREATED);
                    encryptResult = bundle.getInt(POIEmvCoreManager.EmvOnlineConstraints.ENCRYPT_RESULT, PosEmvErrorCode.EMV_UNENCRYPTED);

                    if (vasResult != -1) {
                        Log.d(TAG, "VAS Result : " + vasResult);
                    }
                    if (encryptResult != -1) {
                        Log.d(TAG, "Encrypt Result : " + encryptResult);
                    }

                    vasData = bundle.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.APPLE_DATA);
                    if (vasData != null) {
                        Log.d(TAG, "VAS Data : " + HexUtil.toHexString(vasData));
                    }
                    vasMerchant = bundle.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.APPLE_MERCHANT);
                    if (vasMerchant != null) {
                        Log.d(TAG, "VAS Merchant : " + HexUtil.toHexString(vasMerchant));
                    }
                    data = bundle.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.EMV_DATA);
                    if (data != null) {
                        Log.d(TAG, "Trans Data : " + HexUtil.toHexString(data));
                    }
                    encryptData = bundle.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.ENCRYPT_DATA);
                    if (encryptData != null) {
                        Log.d(TAG, "Encrypt Data : " + HexUtil.toHexString(encryptData));
                    }

                    if (data != null) {
                        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
                        BerTlvParser tlvParser = new BerTlvParser();
                        BerTlvs tlvs = tlvParser.parse(data);
                        for (BerTlv tlv : tlvs.getList()) {
                            tlvBuilder.addBerTlv(tlv);
                        }

                        if (encryptResult == PosEmvErrorCode.EMV_OK && encryptData != null) {
                            BerTlvs encryptTlvs = new BerTlvParser().parse(encryptData);
                            for (BerTlv tlv : encryptTlvs.getList()) {
                                tlvBuilder.addBerTlv(tlv);
                            }
                        }

                        data = tlvBuilder.buildArray();
                    }

                    transData.setTransData(data);

                    if (vasResult != PosEmvErrorCode.APPLE_VAS_UNTREATED) {
                        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
                        if (vasData != null) {
                            BerTlvs tlvs = new BerTlvParser().parse(vasData);
                            for (BerTlv tlv : tlvs.getList()) {
                                tlvBuilder.addBerTlv(tlv);
                            }
                        }

                        if (vasMerchant != null) {
                            BerTlvs tlvs = new BerTlvParser().parse(vasMerchant);
                            for (BerTlv tlv : tlvs.getList()) {
                                tlvBuilder.addBerTlv(tlv);
                            }
                        }

                        transData.setAppleVasResult(vasResult);
                        transData.setAppleVasData(tlvBuilder.buildArray());
                    }

                    Bundle outBundle = new Bundle();

                    outBundle.putInt(POIEmvCoreManager.EmvOnlineConstraints.OUT_AUTH_RESP_CODE, GlobalData.getTransOnlineResult());
                    emvCoreManager.onSetOnlineResponse(outBundle);
                }
            });
        }

        @Override
        public void onTransactionResult(final int result, final Bundle bundle) {
            Log.d(TAG, "onTransactionResult " + result);

            switch (result) {
                case PosEmvErrorCode.EMV_CANCEL:
                case PosEmvErrorCode.EMV_TIMEOUT:
                    onTransEnd(result);
                    return;
                default:
                    break;
            }

            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    boolean isSuccess = result > 0 && result <= 6 && result != 3;
                    //if(result == PosEmvErrorCode.EMV_DECLINED || result == PosEmvErrorCode.EMV_TERMINATED){
                    //    isSuccess = true;
                    //}
                    if (isSuccess) {
                        if (!isBeep) onSuccess();
                    } else {
                        onFailed();
                        //handler.sendEmptyMessage(MOLD_SELECT_TRANS);
                        //return;
                    }

                    byte[] data;
                    int vasResult;
                    byte[] vasData;
                    byte[] vasMerchant;
                    int encryptResult;
                    byte[] encryptData;
                    byte[] scriptResult;

                    vasResult = bundle.getInt(POIEmvCoreManager.EmvResultConstraints.APPLE_RESULT, PosEmvErrorCode.APPLE_VAS_UNTREATED);
                    encryptResult = bundle.getInt(POIEmvCoreManager.EmvResultConstraints.ENCRYPT_RESULT, PosEmvErrorCode.EMV_UNENCRYPTED);

                    if (vasResult != -1) {
                        Log.d(TAG, "VAS Result : " + vasResult);
                    }
                    if (encryptResult != -1) {
                        Log.d(TAG, "Encrypt Result : " + encryptResult);
                    }

                    vasData = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.APPLE_DATA);
                    if (vasData != null) {
                        Log.d(TAG, "VAS Data : " + HexUtil.toHexString(vasData));
                    }
                    vasMerchant = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.APPLE_MERCHANT);
                    if (vasMerchant != null) {
                        Log.d(TAG, "VAS Merchant : " + HexUtil.toHexString(vasMerchant));
                    }
                    data = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.EMV_DATA);
                    if (data != null) {
                        Log.d(TAG, "Trans Data : " + HexUtil.toHexString(data));
                    }
                    encryptData = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.ENCRYPT_DATA);
                    if (encryptData != null) {
                        Log.d(TAG, "Encrypt Data : " + HexUtil.toHexString(encryptData));
                    }
                    scriptResult = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.SCRIPT_RESULT);
                    if (scriptResult != null) {
                        Log.d(TAG, "Script Result : " + HexUtil.toHexString(scriptResult));
                    }

                    if (data != null) {
                        updateCardType(data);
                        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
                        BerTlvParser tlvParser = new BerTlvParser();
                        BerTlvs tlvs = tlvParser.parse(data);
                        for (BerTlv tlv : tlvs.getList()) {
                            tlvBuilder.addBerTlv(tlv);
                            if (tlv.isConstructed()) {
                                Log.d(TAG, String.format("Tag : %1$-4s  >>", tlv.getTag().getBerTagHex()));
                                for (BerTlv value : tlv.getValues()) {
                                    Log.d(TAG, String.format("Tag : %1$-4s", value.getTag().getBerTagHex()) +
                                            " Value : " + value.getHexValue());
                                }
                                Log.d(TAG, String.format("Tag : %1$-4s  <<", tlv.getTag().getBerTagHex()));
                            } else {
                                Log.d(TAG, String.format("Tag : %1$-4s", tlv.getTag().getBerTagHex()) + " Value : " + tlv.getHexValue());
                            }
                        }

                        if (encryptResult == PosEmvErrorCode.EMV_OK && encryptData != null) {
                            BerTlvs encryptTlvs = new BerTlvParser().parse(encryptData);
                            for (BerTlv tlv : encryptTlvs.getList()) {
                                tlvBuilder.addBerTlv(tlv);
                                Log.d(TAG, String.format("Tag : %1$-4s", tlv.getTag().getBerTagHex()) + " Value : " + tlv.getHexValue());
                            }
                        }

                        data = tlvBuilder.buildArray();
                    }

                    switch (result) {
                        case PosEmvErrorCode.EMV_MULTI_CONTACTLESS:
                            Log.e(TAG, "Multiple cards , Present a single card");
                            onTransStart();
                            return;
                        case PosEmvErrorCode.EMV_FALLBACK:
                            isFallBack = true;
                            Log.e(TAG, "Please Magnetic Stripe");
                            Log.e(TAG, "FallBack");
                            onTransStart();
                            return;
                        case PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE:
                            Log.e(TAG, "Please Insert Card");
                            onTransStart();
                            return;
                        case PosEmvErrorCode.EMV_APP_EMPTY:
                            isFallBack = true;
                            Log.e(TAG, "Please Magnetic Stripe");
                            Log.e(TAG, "AID Empty");
                            onTransStart();
                            return;
                        case PosEmvErrorCode.EMV_SEE_PHONE:
                        case PosEmvErrorCode.APPLE_VAS_WAITING_INTERVENTION:
                        case PosEmvErrorCode.APPLE_VAS_WAITING_ACTIVATION:
                            Log.e(TAG, "Please See Phone");
                            onTransStart();
                            return;
                        default:
                            break;
                    }

                    if (vasResult != PosEmvErrorCode.APPLE_VAS_UNTREATED) {
                        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
                        if (vasData != null) {
                            BerTlvs tlvs = new BerTlvParser().parse(vasData);
                            for (BerTlv tlv : tlvs.getList()) {
                                tlvBuilder.addBerTlv(tlv);
                            }
                        }

                        if (vasMerchant != null) {
                            BerTlvs tlvs = new BerTlvParser().parse(vasMerchant);
                            for (BerTlv tlv : tlvs.getList()) {
                                tlvBuilder.addBerTlv(tlv);
                            }
                        }
                        transData.setAppleVasResult(vasResult);
                        transData.setAppleVasData(tlvBuilder.buildArray());
                    }

                    transData.setTransData(data);
                    transData.setTransResult(result);

                    boolean isCardReadSuccess = false;
                    if (data != null) {
                        EmvCard emvCard = new EmvCard(data);
                        if (emvCard.getCardNumber() != null) {
                            //transData.setTransResult(PosEmvErrorCode.EMV_APPROVED);  //临时修改
                            transRepository.createTransaction(transData);
                            isCardReadSuccess = true;
                        }
                    } else if (vasData != null) {
                        if (vasResult == PosEmvErrorCode.APPLE_VAS_APPROVED) {
                            transRepository.createTransaction(transData);
                        }
                    }

                    boolean finalIsCardReadSuccess = isCardReadSuccess;
                    long delayTime = finalIsCardReadSuccess ? 1 : 1500;
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (finalIsCardReadSuccess) {
                                paymentResultCallback(isSuccess, "", result);
                            } else {
                                onTransStart();
                            }
                        }
                    }, delayTime);
                }
            });
        }
    }

    private void updateCardType(byte[] data) {
        if (transData.getCardType() != POIEmvCoreManager.EMV_CARD_VISA) {
            return;
        }

        BerTlvParser tlvParser = new BerTlvParser();
        BerTlvs tlvs = tlvParser.parse(data);
        BerTlv tlv = tlvs.find(new BerTag("9F06"));
        if (tlv != null && tlv.getHexValue().contains("A000000333")) {
            transData.setCardType(POIEmvCoreManager.EMV_CARD_UNIONPAY);
        }
    }
    // 付款 & 回调--->end
}
