package com.xc.apex.nre.lib_payment.emv;

import java.util.ArrayList;
import java.util.List;

public enum OnlineConfig {

    DEFAULT(0, "Default"),
    UL(1, "UL");

    private final int    type;
    private final String name;

    OnlineConfig(final int type, final String name) {
        this.type = type;
        this.name = name;
    }

    public static List<String> getOnlineConfig() {
        List<String> list = new ArrayList<>();
        for (OnlineConfig val : OnlineConfig.values()) {
            list.add(val.name);
        }
        return list;
    }

    public static int getOnlineConfig(String name) {
        int ret = DEFAULT.type;
        for (OnlineConfig val : OnlineConfig.values()) {
            if (val.name.equals(name)) {
                ret = val.type;
                break;
            }
        }
        return ret;
    }

    public static String getOnlineConfig(int name) {
        String ret = DEFAULT.name;
        for (OnlineConfig val : OnlineConfig.values()) {
            if (val.type == name) {
                ret = val.name;
                break;
            }
        }
        return ret;
    }

    public static OnlineConfig getConfig(int type) {
        OnlineConfig ret = DEFAULT;
        for (OnlineConfig val : OnlineConfig.values()) {
            if (val.type == type) {
                ret = val;
                break;
            }
        }
        return ret;
    }
}
