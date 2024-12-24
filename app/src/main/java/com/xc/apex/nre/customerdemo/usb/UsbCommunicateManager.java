package com.xc.apex.nre.customerdemo.usb;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xc.apex.nre.customerdemo.BaseApplication;
import com.xc.apex.nre.customerdemo.NREConstant;
import com.xc.apex.nre.customerdemo.R;
import com.xc.apex.nre.customerdemo.usb.command.ReceiverCommand;
import com.xc.apex.nre.customerdemo.usb.event.FinishEvent;
import com.xc.apex.nre.customerdemo.usb.event.PayCompleted;
import com.xc.apex.nre.customerdemo.usb.event.PayEvent;
import com.xc.apex.nre.customerdemo.utils.CommonUtil;
import com.xc.apex.nre.customerdemo.utils.ToastUtil;
import com.xc.apex.nre.lib_usb.CommunicateManager;
import com.xc.apex.nre.lib_usb.IReceiveListener;

import org.greenrobot.eventbus.EventBus;

public class UsbCommunicateManager implements IReceiveListener {

    private static final String TAG = "UsbCommunicateManager";

    private static volatile UsbCommunicateManager instance;
    private Handler uiHandler;
    private Context context;

    public static UsbCommunicateManager getInstance(Context serviceCxt) {
        if (instance == null) {
            synchronized (UsbCommunicateManager.class) {
                if (instance == null) {
                    instance = new UsbCommunicateManager(serviceCxt);
                }
            }
        }
        return instance;
    }

    private UsbCommunicateManager(Context serviceCxt) {
        this.context = serviceCxt;
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public void receiveUsbCommunicateListener() {
        Log.d(TAG, "receiveUsbCommunicateListener");
        CommunicateManager.getInstance(context.getApplicationContext()).setReceiveListener(this::onReceiveData);
        showToast(BaseApplication.getContext().getResources().getString(R.string.txt_register_usb_completed));
    }

    @Override
    public void onReceiveData(byte[] data) {
        Log.e(TAG, "onReceiveData");
        if (data != null && data.length > 0) {
            handleCommand(new String(data));
        }
    }

    private void handleCommand(String command) {
        Log.e(TAG, "handleCommand:: command = " + command);
        usbCallbackVisualization(command);

        if (command.equalsIgnoreCase(ReceiverCommand.START_CUSTOMER_APK)) { // 启动副屏应用
            boolean isCustomerApkRunning = CommonUtil.isAppInForeground(context, NREConstant.PKG_NAME);
            Log.d(TAG, "isCustomerApkRunning = " + isCustomerApkRunning);
            usbCallbackVisualization("isCustomerApkRunning = " + isCustomerApkRunning);

            if (!isCustomerApkRunning) {
                CommonUtil.launchApkByService(context, NREConstant.PKG_NAME);
            }
        } else if (command.equalsIgnoreCase(ReceiverCommand.START_ORDERING)) { // 开始点单
            boolean isPageInForeground = CommonUtil.isTargetPageInForeground(context, NREConstant.DISCOUNT_PAGE_CLS_NAME);
            Log.d(TAG, "DISCOUNT_PAGE:: isPageInForeground = " + isPageInForeground);
            usbCallbackVisualization("DISCOUNT_PAGE:: isPageInForeground = " + isPageInForeground);

            EventBus.getDefault().post(new FinishEvent(command));

            if (!isPageInForeground) {
                CommonUtil.launchTargetPageByService(context, NREConstant.PKG_NAME, NREConstant.DISCOUNT_PAGE_CLS_NAME);
            }
        } else if (command.equalsIgnoreCase(ReceiverCommand.PAY_BY_CASH)
                || command.equalsIgnoreCase(ReceiverCommand.PAY_BY_CARD)
                || command.equalsIgnoreCase(ReceiverCommand.PAY_BY_QRCODE)) { // 切换付款方式

            boolean isPageInForeground = CommonUtil.isTargetPageInForeground(context, NREConstant.PAY_PAGE_CLS_NAME);
            Log.d(TAG, "PAY_PAGE:: isPageInForeground = " + isPageInForeground);
            usbCallbackVisualization("PAY_PAGE:: isPageInForeground = " + isPageInForeground);

            EventBus.getDefault().post(new FinishEvent(command));

            if (!isPageInForeground) {
                CommonUtil.launchTargetPageByService(context, NREConstant.PKG_NAME, NREConstant.PAY_PAGE_CLS_NAME);
            }

            EventBus.getDefault().post(new PayEvent(command));
        } else if (command.equalsIgnoreCase(ReceiverCommand.CANCEL_CHARGE)) { // 取消付款
            EventBus.getDefault().post(new FinishEvent(command));
        } else if (command.equalsIgnoreCase(ReceiverCommand.CASH_CHARGE_SUCCESS)) { // 现金支付成功
            EventBus.getDefault().post(new PayCompleted(true));
        } else {
            boolean isStartCharge = command.indexOf(ReceiverCommand.START_CHARGE) > 0;
            usbCallbackVisualization("isStartCharge = " + isStartCharge);
            if (isStartCharge) { // 开始付款
                boolean isPageInForeground = CommonUtil.isTargetPageInForeground(context, NREConstant.PAY_PAGE_CLS_NAME);
                EventBus.getDefault().post(new FinishEvent(command));
                CommonUtil.launchTargetPageByService(context, NREConstant.PKG_NAME, NREConstant.PAY_PAGE_CLS_NAME, NREConstant.KEY_ORDER_DATA, command);
            }
        }
    }

    public void sendCommandToHost(String command) {
        Log.d(TAG, "sendCommandToHost:: command = " + command);
        CommunicateManager.getInstance(context.getApplicationContext()).sendData(command.getBytes());
    }

    private void showToast(String msg) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(BaseApplication.getContext(), msg);
            }
        });
    }

    // TODO start
    private void usbCallbackVisualization(String data) {
//        uiHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                ToastUtil.showToast(BaseApplication.getContext(), "C-R = " + data);
//            }
//        });
    }
    // end
}
