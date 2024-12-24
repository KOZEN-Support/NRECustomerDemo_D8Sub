package com.xc.apex.nre.customerdemo.usb.event;

public class FinishEvent {
    private String command;

    public FinishEvent(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
