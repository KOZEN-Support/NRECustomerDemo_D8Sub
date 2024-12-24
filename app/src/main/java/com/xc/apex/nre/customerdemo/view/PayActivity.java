package com.xc.apex.nre.customerdemo.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.xc.apex.nre.customerdemo.BaseApplication;
import com.xc.apex.nre.customerdemo.NREConstant;
import com.xc.apex.nre.customerdemo.R;
import com.xc.apex.nre.customerdemo.databinding.ActivityPayBinding;
import com.xc.apex.nre.customerdemo.model.OrderBean;
import com.xc.apex.nre.customerdemo.usb.UsbCommunicateManager;
import com.xc.apex.nre.customerdemo.usb.command.SendCommand;
import com.xc.apex.nre.customerdemo.usb.event.FinishEvent;
import com.xc.apex.nre.customerdemo.usb.event.PayCompleted;
import com.xc.apex.nre.customerdemo.usb.event.PayEvent;
import com.xc.apex.nre.customerdemo.usb.command.ReceiverCommand;
import com.xc.apex.nre.customerdemo.utils.ToastUtil;
import com.xc.apex.nre.customerdemo.view.adapter.OrderAdapter;
import com.xc.apex.nre.customerdemo.view.base.BaseActivity;
import com.xc.apex.nre.lib_payment.PaymentManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;

/**
 * 支付界面
 */
public class PayActivity extends BaseActivity implements PaymentManager.PaymentResultListener, QRCodeView.Delegate {
    private static final String TAG = "PayActivity";

    // 支付状态
    private static final int PAY_CASH = 0;
    private static final int PAY_CARD = 1;
    private static final int PAY_QRCODE = 2;
    private static final int PAY_SUCCESS = 3;
    private static final int PAY_FAILED = 4;

    private ActivityPayBinding binding;
    private OrderAdapter orderAdapter;
    private OrderBean orderDataBean;
    private String totalTicketVal = "0.0";
    private boolean isCameraOpened = false; // 只要开启，在界面被关闭的时候再关闭（快速切换回报错）

    private final Handler handler = new Handler();
    // 支付超时
    private static final int DELAY_PAY_TIMEOUT = 20 * 1000;
    private Runnable showResultRunnable = new Runnable() {
        @Override
        public void run() {
            payCompletedAndSendCommand(false);
        }
    };
    // 显示结果界面的时间，时间到了自动关闭页面
    private static final int DELAY_CLOSE_PAGE_TIME = 10 * 1000;
    private Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay);

        // 绑定扫码相机
        binding.scanView.setDelegate(this);
        checkPermissionAndCamera();

        EventBus.getDefault().register(this);

        // 获取订单数据
        Intent intent = getIntent();
        loadOrderData(intent);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadOrderData(intent);
        initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishEvent(FinishEvent event) {
        Log.d(TAG, "[EventBus]onFinishEvent command = " + event.getCommand());
        String command = event.getCommand();
        boolean isStartCharge = command.indexOf(ReceiverCommand.START_CHARGE) > 0;
        if (!command.equalsIgnoreCase(ReceiverCommand.PAY_BY_CASH)
                && !command.equalsIgnoreCase(ReceiverCommand.PAY_BY_CARD)
                && !command.equalsIgnoreCase(ReceiverCommand.PAY_BY_QRCODE)
                && !isStartCharge) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayEvent(PayEvent event) {
        Log.d(TAG, "[EventBus]onPayEvent command = " + event.getCommand());
        String command = event.getCommand();
        String payType = "null";
        if (command.equalsIgnoreCase(ReceiverCommand.PAY_BY_CASH)) {
            payType = "Cash";
            resetPayModeState(PAY_CASH, false);

            // 关闭卡支付
            closeCardPaymentAccess();
        } else if (command.equalsIgnoreCase(ReceiverCommand.PAY_BY_CARD)) {
            payType = "Card";
            resetPayModeState(PAY_CARD, false);

            // 开启卡支付
            openCardPaymentAccess();
        } else if (command.equalsIgnoreCase(ReceiverCommand.PAY_BY_QRCODE)) {
            payType = "QRCode";
            resetPayModeState(PAY_QRCODE, false);

            // 关闭卡支付
            closeCardPaymentAccess();
            // 开启camera支付
            Log.d(TAG, "isCameraOpened = " + isCameraOpened);
            if (!isCameraOpened) {
                startSpot();
            }
        }

//        ToastUtil.showToast(PayActivity.this, "payType = " + payType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayCompleted(PayCompleted event) { // 现金支付成功：主屏通知副屏更新页面状态
        Log.d(TAG, "[EventBus]onPayCompleted = " + event.isSuccess());
        resetPayModeState(event.isSuccess() ? PAY_SUCCESS : PAY_FAILED, true);
    }

    private void resetShowResultTimer() {
        handler.removeCallbacks(closeRunnable);
        handler.removeCallbacks(showResultRunnable);
        handler.postDelayed(showResultRunnable, DELAY_PAY_TIMEOUT);
    }

    private void resetCloseTimer() {
        handler.removeCallbacks(closeRunnable);
        handler.removeCallbacks(showResultRunnable);
        handler.postDelayed(closeRunnable, DELAY_CLOSE_PAGE_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacks(showResultRunnable);
        handler.removeCallbacks(closeRunnable);
        stopSpot();
        closeCardPaymentAccess();
        binding.scanView.onDestroy(); // 销毁二维码扫描控件
    }

    private void initView() {
        if (orderDataBean != null) {
            // 订单-总值
            totalTicketVal = orderDataBean.getTotal();
            binding.tvTotalValue.setText("$" + totalTicketVal);
            binding.tvCrashTotalValue.setText("$" + totalTicketVal);
            binding.tvCardTotalValue.setText("$" + totalTicketVal);
            binding.tvQrcodeValue.setText("$" + totalTicketVal);

            // 订单列表
            if (orderDataBean.getOrderList() != null) {
                orderAdapter = new OrderAdapter(this, orderDataBean.getOrderList());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                binding.rvOrder.setLayoutManager(layoutManager);
                binding.rvOrder.setAdapter(orderAdapter);
            }
        }
        // 更新页面状态和总值
        resetPayModeState(PAY_CASH, false);
        // 关闭卡支付
        closeCardPaymentAccess();
    }

    private void loadOrderData(Intent intent) {
        if (intent != null) {
            String orderDataStr = intent.getStringExtra(NREConstant.KEY_ORDER_DATA);
            if (orderDataStr != null) {
                Gson gson = new Gson();
                orderDataBean = gson.fromJson(orderDataStr, OrderBean.class);
                Log.d(TAG, "orderDataBean = " + orderDataBean.toString());
//                ToastUtil.showToast(PayActivity.this, "orderData = " + orderDataBean.toString());
            }
        }
    }

    private void resetPayModeState(int type, boolean isComplete) {
        // 刷新支付结果状态
        binding.layoutPayOver.setVisibility(isComplete ? View.VISIBLE : View.GONE);
        binding.tvPayResult.setText(isComplete && type == PAY_SUCCESS ? getString(R.string.txt_pay_success) : getString(R.string.txt_pay_failed));
        // 更新gif动画
        int payResultGif = isComplete && type == PAY_SUCCESS ? R.drawable.gif_pay_success : R.drawable.gif_pay_failed;
        Glide.with(this)
                .asGif()
                .load(payResultGif)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.ivPayResult);
        // 刷新支付方式的状态
        binding.layoutPayCash.setVisibility(!isComplete && type == PAY_CASH ? View.VISIBLE : View.GONE);
        binding.layoutPayCard.setVisibility(!isComplete && type == PAY_CARD ? View.VISIBLE : View.GONE);
        binding.layoutPayQrcode.setVisibility(!isComplete && type == PAY_QRCODE ? View.VISIBLE : View.GONE);

        if (isComplete) {
            // 10s后关闭界面
            resetCloseTimer();
        } else {
            // 20s后显示结果界面，支付超时
            resetShowResultTimer();
        }
    }

    private void openCardPaymentAccess() {
        if (PaymentManager.getInstance(BaseApplication.getContext()).isPaymentSdkAvailable()) {
            Log.d(TAG, "[openCardPaymentAccess]Begin to pay");
            PaymentManager.getInstance(BaseApplication.getContext()).startToTrans(totalTicketVal, true, PayActivity.this, this::onPaymentSuccess);
        }
    }

    private void closeCardPaymentAccess() {
        if (PaymentManager.getInstance(BaseApplication.getContext()).isPaymentSdkAvailable()) {
            PaymentManager.getInstance(BaseApplication.getContext()).shutdownTrans();
        }
    }

    @Override
    public void onPaymentSuccess(boolean paySuccess, String msg, int resultCode) {
        Log.e(TAG, "onPaymentSuccess:: " + paySuccess + " , resultCode = " + resultCode);
        // 成功的时候显示支付成功界面
        // 失败的时候，除了“EMV_CANCEL=-1”外，都需要显示支付失败界面
        if (paySuccess || (!paySuccess && resultCode != -1)) {
            payCompletedAndSendCommand(paySuccess);
        }
    }

    private void payCompletedAndSendCommand(boolean isPaySuccess) {
        // 关闭卡支付
        PaymentManager.getInstance(BaseApplication.getContext()).shutdownTrans();

        resetPayModeState(isPaySuccess ? PAY_SUCCESS : PAY_FAILED, true);
        if (isPaySuccess) {
            UsbCommunicateManager.getInstance(BaseApplication.getContext()).sendCommandToHost(SendCommand.PAY_SUCCESS);
        } else {
            UsbCommunicateManager.getInstance(BaseApplication.getContext()).sendCommandToHost(SendCommand.PAY_FAILED);
        }
    }

    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, NREConstant.REQUEST_CODE_QRCODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NREConstant.REQUEST_CODE_QRCODE_PERMISSIONS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //用户拒绝权限
                Log.e(TAG, "The user has refused camera permission.");
                ToastUtil.showToast(this, getString(R.string.txt_permission_failed));
            }
        }
    }

    /**
     * 开启camera扫码
     */
    private void startSpot() {
        new Thread(() -> {
            Log.d(TAG, "startSpotAndShowRect+");
            binding.scanView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            binding.scanView.setType(BarcodeType.TWO_DIMENSION, null); // 识别所有类型的二维码
            binding.scanView.startSpotAndShowRect(); // 显示扫描框，并开始识别
            isCameraOpened = true;
        }).start();
    }

    /**
     * 关闭camera扫码
     */
    private void stopSpot() {
        Log.d(TAG, "stopSpot+");
        binding.scanView.stopCamera();
        isCameraOpened = false;
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.d(TAG, "onScanQRCodeSuccess");
        // 扫码成功
        payCompletedAndSendCommand(true);
        // 播放支付成功提示音
        PaymentManager.getInstance(BaseApplication.getContext()).playSuccessBeep();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        // 打开相机出错
        Log.e(TAG, "onScanQRCodeOpenCameraError");
    }
}
