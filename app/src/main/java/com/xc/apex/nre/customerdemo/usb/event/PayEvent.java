package com.xc.apex.nre.customerdemo.usb.event;

public class PayEvent {

    private String command;

    public PayEvent(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
