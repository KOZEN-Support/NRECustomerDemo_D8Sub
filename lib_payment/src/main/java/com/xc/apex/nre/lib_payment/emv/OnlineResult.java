package com.xc.apex.nre.lib_payment.emv;

import com.pos.sdk.emvcore.POIEmvCoreManager.EmvOnlineConstraints;

import java.util.ArrayList;
import java.util.List;

public enum OnlineResult {

    APPROVE(EmvOnlineConstraints.EMV_ONLINE_APPROVE, "Approve"),
    FAIL(EmvOnlineConstraints.EMV_ONLINE_FAIL, "Fail"),
    DENIAL(EmvOnlineConstraints.EMV_ONLINE_DENIAL, "Denial"),
    REFER_TO_CARD_ISSUER(EmvOnlineConstraints.EMV_ONLINE_REFER_TO_CARD_ISSUER, "Refer To Card Issuer");

    private final int    type;
    private final String name;

    OnlineResult(final int type, final String name) {
        this.type = type;
        this.name = name;
    }

    public static List<String> getOnlineResult() {
        List<String> list = new ArrayList<>();
        for (OnlineResult val : OnlineResult.values()) {
            list.add(val.name);
        }
        return list;
    }

    public static int getOnlineResult(String name) {
        int ret = APPROVE.type;
        for (OnlineResult val : OnlineResult.values()) {
            if (val.name.equals(name)) {
                ret = val.type;
                break;
            }
        }
        return ret;
    }

    public static String getOnlineResult(int type) {
        String ret = APPROVE.name;
        for (OnlineResult val : OnlineResult.values()) {
            if (val.type == type) {
                ret = val.name;
                break;
            }
        }
        return ret;
    }
}
