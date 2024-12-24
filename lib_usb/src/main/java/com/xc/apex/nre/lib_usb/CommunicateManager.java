package com.xc.apex.nre.lib_usb;

import android.content.Context;
import android.usbcommunicate.IXcUsbListener;
import android.usbcommunicate.XcUsbManager;

public class CommunicateManager {
    private static volatile CommunicateManager instance;
    private XcUsbManager mXcUsbManager;
    private IReceiveListener mReceiveListener;

    public static CommunicateManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CommunicateManager.class) {
                if (instance == null) {
                    instance = new CommunicateManager(context);
                }
            }
        }
        return instance;
    }

    private CommunicateManager(Context context) {
        mXcUsbManager = (XcUsbManager) context.getSystemService(Context.XC_USB_SERVICE);
    }

    public boolean sendData(byte[] data) {
        try {
            return mXcUsbManager.sendData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setReceiveListener(IReceiveListener listener) {
        mReceiveListener = listener;
        mXcUsbManager.setReceiveListener(new IXcUsbListener.Stub() {
            @Override
            public void onReceiveData(byte[] data) {
                mReceiveListener.onReceiveData(data);
            }
        });
    }
}
