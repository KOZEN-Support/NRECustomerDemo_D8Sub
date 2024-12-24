package com.xc.apex.nre.lib_payment.utils;

public class RegexUtils {

    public static final String NUMBER = "^[0-9]{2,}$";

    public static final String CHARACTER = "^[A-Za-z0-9_\\s]+$";

    public static boolean isNumber(String string) {
        return string.matches(NUMBER);
    }

    public static boolean isCharacter(String string) {
        return string.matches(CHARACTER);
    }
}
