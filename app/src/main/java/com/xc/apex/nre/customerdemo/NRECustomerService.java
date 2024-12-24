package com.xc.apex.nre.customerdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xc.apex.nre.customerdemo.usb.UsbCommunicateManager;
import com.xc.apex.nre.lib_payment.PaymentManager;

public class NRECustomerService extends Service {
    private static final String TAG = "NRECustomerService";
    private static final String CHANNEL_ID = "NRECustomerService";
    private static final String CHANNEL_NAME = "High Priority";

    private static final String HANDLER_THREAD_NAME = "NRECustomerCtrlHandler";
    private static final int DELAY_REGISTER_USB_LISTENER_TIME = 8 * 1000;
    private Handler ctrlHandler;
    private static final int MSG_REGISTER_USB_LISTENER = 0x1001;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NRECustomerService onCreate");
        // 创建通知渠道
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "NRECustomerService onStartCommand");
        // 创建通知
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle(getResources().getString(R.string.txt_service_notification_title))
                .setContentText(getResources().getString(R.string.txt_service_notification_content))
                .setSmallIcon(R.drawable.ic_logo);

        // 将服务设置为前台服务
        startForeground(1, builder.build());

        // 延时10s后注册usb通讯回调
        getCtrlHandler().removeMessages(MSG_REGISTER_USB_LISTENER);
        getCtrlHandler().sendEmptyMessageDelayed(MSG_REGISTER_USB_LISTENER, DELAY_REGISTER_USB_LISTENER_TIME);

        // 初始化金融SDK
        PaymentManager.getInstance(BaseApplication.getContext()).initPaymentSdk(new PaymentManager.InitPaymentCallbackListener() {
            @Override
            public void onInitResult(boolean isSuccess, Exception e) {
                Log.e(TAG, "Payment SDK init " + (isSuccess ? "Success." : "Failed:: " + e.getMessage()));
            }
        });

        // 返回粘性启动模式，以便服务在意外终止后能重新启动
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "NRECustomerService onDestroy");
    }

    private Handler getCtrlHandler() {
        if (ctrlHandler == null || !ctrlHandler.getLooper().getThread().isAlive()) {
            HandlerThread backgroundThread =
                    new HandlerThread(HANDLER_THREAD_NAME, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            backgroundThread.start();

            if (backgroundThread.isAlive()) {
                ctrlHandler = new Handler(backgroundThread.getLooper(), ctrlCallback);
            } else {
                Log.e(TAG, "Failed to start getCtrlHandler.");
            }
        }
        return ctrlHandler;
    }

    private final Handler.Callback ctrlCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Log.d(TAG, "handleMessage:: " + message.what);
            switch (message.what) {
                case MSG_REGISTER_USB_LISTENER:
                    Log.d(TAG, "Register USB communication callback");
                    UsbCommunicateManager.getInstance(NRECustomerService.this).receiveUsbCommunicateListener();
                    break;
            }
            return true;
        }
    };

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
