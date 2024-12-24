package com.xc.apex.nre.customerdemo.usb.event;

public class PayCompleted {
    boolean isSuccess = false;

    public PayCompleted(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
