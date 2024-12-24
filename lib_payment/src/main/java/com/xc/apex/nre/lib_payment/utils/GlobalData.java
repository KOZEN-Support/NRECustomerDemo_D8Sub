package com.xc.apex.nre.lib_payment.utils;

public class GlobalData {

    private static final String TRANS_TYPE                = "trans_type";
    private static final String TRANS_COUNTER             = "trans_counter";
    private static final String TRANS_SUPPORT_APPLE       = "trans_support_apple";
    private static final String TRANS_SUPPORT_GOOGLE      = "trans_support_google";
    private static final String TRANS_SUPPORT_CONTACT     = "trans_support_contact";
    private static final String TRANS_SUPPORT_CONTACTLESS = "trans_support_contactless";
    private static final String TRANS_SUPPORT_MAGSTRIPE   = "trans_support_magstripe";
    private static final String TRANS_ONLINE_CONFIG       = "trans_online_config";
    private static final String TRANS_ONLINE_RESULT       = "trans_online_result";
    private static final String TRANS_KEYBOARD_FIX        = "trans_keyboard_fix";

    private static final String TRANS_ENCRYPT_CONTACT     = "trans_encrypt_contact";
    private static final String TRANS_ENCRYPT_CONTACTLESS = "trans_encrypt_contactless";
    private static final String TRANS_ENCRYPT_MAGSTRIPE   = "trans_encrypt_magstripe";

    private static final String TRANS_ENCRYPT_TYPE               = "trans_encrypt_type";
    private static final String TRANS_ENCRYPT_INDEX              = "trans_encrypt_index";
    private static final String TRANS_ENCRYPT_PADDING            = "trans_encrypt_padding";
    private static final String TRANS_ENCRYPT_MODE               = "trans_encrypt_mode";
    private static final String TRANS_ENCRYPT_VECTOR             = "trans_encrypt_vector";
    private static final String TRANS_ENCRYPT_TRANS_ARMOR_POS_ID = "trans_encrypt_trans_armor_pos_id";
    private static final String TRANS_ENCRYPT_TRANS_ARMOR_KEY_ID = "trans_encrypt_trans_armor_key_id";
    private static final String TRANS_ENCRYPT_BASE64             = "trans_encrypt_base64";

    private static final String TRANS_TYPE_CONTACT     = "_contact";
    private static final String TRANS_TYPE_CONTACTLESS = "_contactless";
    private static final String TRANS_TYPE_MAGSTRIPE   = "_magstripe";

    public static void setTransType(int type) {
        SPUtils.getInstance().put(TRANS_TYPE, type);
    }

    public static int getTransType() {
        return SPUtils.getInstance().getInt(TRANS_TYPE);
    }

    public static void setSupportAppleVas(boolean support) {
        SPUtils.getInstance().put(TRANS_SUPPORT_APPLE, support);
    }

    public static boolean isSupportAppleVas() {
        return SPUtils.getInstance().getBoolean(TRANS_SUPPORT_APPLE, false);
    }

    public static void setSupportGoogleSmartTap(boolean support) {
        SPUtils.getInstance().put(TRANS_SUPPORT_GOOGLE, support);
    }

    public static boolean isSupportGoogleSmartTap() {
        return SPUtils.getInstance().getBoolean(TRANS_SUPPORT_GOOGLE, false);
    }

    public static void setSupportContact(boolean support) {
        SPUtils.getInstance().put(TRANS_SUPPORT_CONTACT, support);
    }

    public static boolean isSupportContact() {
        return SPUtils.getInstance().getBoolean(TRANS_SUPPORT_CONTACT, true);
    }

    public static void setSupportContactless(boolean support) {
        SPUtils.getInstance().put(TRANS_SUPPORT_CONTACTLESS, support);
    }

    public static boolean isSupportContactless() {
        return SPUtils.getInstance().getBoolean(TRANS_SUPPORT_CONTACTLESS, true);
    }

    public static void setSupportMagstripe(boolean support) {
        SPUtils.getInstance().put(TRANS_SUPPORT_MAGSTRIPE, support);
    }

    public static boolean isSupportMagstripe() {
        return SPUtils.getInstance().getBoolean(TRANS_SUPPORT_MAGSTRIPE, true);
    }

    public static String getTransCounter() {
        int counter = SPUtils.getInstance().getInt(TRANS_COUNTER, 1);
        String str = counter + "";
        StringBuilder builder = new StringBuilder();
        for (int y = str.length(); y < 4; y++) {
            builder.append("0");
        }
        builder.append(str);
        setTransCounter(1 + counter);
        return builder.toString();
    }

    private static void setTransCounter(int counter) {
        if (counter >= 9999) {
            counter = 1;
        }
        SPUtils.getInstance().put(TRANS_COUNTER, counter, true);
    }

    public static void setTransOnlineConfig(int type) {
        SPUtils.getInstance().put(TRANS_ONLINE_CONFIG, type);
    }

    public static int getTransOnlineConfig() {
        return SPUtils.getInstance().getInt(TRANS_ONLINE_CONFIG, 0);
    }

    public static void setTransOnlineResult(int type) {
        SPUtils.getInstance().put(TRANS_ONLINE_RESULT, type);
    }

    public static int getTransOnlineResult() {
        return SPUtils.getInstance().getInt(TRANS_ONLINE_RESULT, 0);
    }

    public static void setKeyboardFix(boolean support) {
        SPUtils.getInstance().put(TRANS_KEYBOARD_FIX, support);
    }

    public static boolean isKeyboardFix() {
        return SPUtils.getInstance().getBoolean(TRANS_KEYBOARD_FIX, true);
    }

//    public static void setTransEncryptContact(boolean open) {
//        SPUtils.getInstance().put(TRANS_ENCRYPT_CONTACT, open);
//    }
//
//    public static boolean getTransEncryptContact() {
//        return SPUtils.getInstance().getBoolean(TRANS_ENCRYPT_CONTACT);
//    }
//
//    public static void setTransEncryptContactless(boolean open) {
//        SPUtils.getInstance().put(TRANS_ENCRYPT_CONTACTLESS, open);
//    }
//
//    public static boolean getTransEncryptContactless() {
//        return SPUtils.getInstance().getBoolean(TRANS_ENCRYPT_CONTACTLESS);
//    }
//
//    public static void setTransEncryptMagstripe(boolean open) {
//        SPUtils.getInstance().put(TRANS_ENCRYPT_MAGSTRIPE, open);
//    }
//
//    public static boolean getTransEncryptMagstripe() {
//        return SPUtils.getInstance().getBoolean(TRANS_ENCRYPT_MAGSTRIPE);
//    }
//
//    public static void setTransEncryptType(CardType cardType, int type) {
//        switch (cardType) {
//            case CONTACT:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TYPE + TRANS_TYPE_CONTACT, type);
//                break;
//            case CONTACTLESS:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TYPE + TRANS_TYPE_CONTACTLESS, type);
//                break;
//            case MAGSTRIPE:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TYPE + TRANS_TYPE_MAGSTRIPE, type);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static int getTransEncryptType(CardType cardType) {
//        int result = EncryptKeyType.ENCRYPT_KEY_TYPE_DUKPT_PIN.type;
//
//        switch (cardType) {
//            case CONTACT:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_TYPE + TRANS_TYPE_CONTACT,
//                        EncryptKeyType.ENCRYPT_KEY_TYPE_DUKPT_PIN.type);
//                break;
//            case CONTACTLESS:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_TYPE + TRANS_TYPE_CONTACTLESS,
//                        EncryptKeyType.ENCRYPT_KEY_TYPE_DUKPT_PIN.type);
//                break;
//            case MAGSTRIPE:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_TYPE + TRANS_TYPE_MAGSTRIPE,
//                        EncryptKeyType.ENCRYPT_KEY_TYPE_DUKPT_PIN.type);
//                break;
//            default:
//                break;
//        }
//
//        return result;
//    }
//
//    public static void setTransEncryptIndex(CardType cardType, int index) {
//        switch (cardType) {
//            case CONTACT:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_INDEX + TRANS_TYPE_CONTACT, index);
//                break;
//            case CONTACTLESS:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_INDEX + TRANS_TYPE_CONTACTLESS, index);
//                break;
//            case MAGSTRIPE:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_INDEX + TRANS_TYPE_MAGSTRIPE, index);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static int getTransEncryptIndex(CardType cardType) {
//        int result = 1;
//
//        switch (cardType) {
//            case CONTACT:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_INDEX + TRANS_TYPE_CONTACT, 1);
//                break;
//            case CONTACTLESS:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_INDEX + TRANS_TYPE_CONTACTLESS, 1);
//                break;
//            case MAGSTRIPE:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_INDEX + TRANS_TYPE_MAGSTRIPE, 1);
//                break;
//            default:
//                break;
//        }
//
//        return result;
//    }
//
//    public static void setTransEncryptPadding(CardType cardType, String padding) {
//        switch (cardType) {
//            case CONTACT:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_PADDING + TRANS_TYPE_CONTACT, padding);
//                break;
//            case CONTACTLESS:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_PADDING + TRANS_TYPE_CONTACTLESS, padding);
//                break;
//            case MAGSTRIPE:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_PADDING + TRANS_TYPE_MAGSTRIPE, padding);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static String getTransEncryptPadding(CardType cardType) {
//        String result = "0";
//
//        switch (cardType) {
//            case CONTACT:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_PADDING + TRANS_TYPE_CONTACT, "0");
//                break;
//            case CONTACTLESS:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_PADDING + TRANS_TYPE_CONTACTLESS, "0");
//                break;
//            case MAGSTRIPE:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_PADDING + TRANS_TYPE_MAGSTRIPE, "0");
//                break;
//            default:
//                break;
//        }
//
//        return result;
//    }
//
//    public static void setTransEncryptMode(CardType cardType, int mode) {
//        switch (cardType) {
//            case CONTACT:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_MODE + TRANS_TYPE_CONTACT, mode);
//                break;
//            case CONTACTLESS:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_MODE + TRANS_TYPE_CONTACTLESS, mode);
//                break;
//            case MAGSTRIPE:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_MODE + TRANS_TYPE_MAGSTRIPE, mode);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static int getTransEncryptMode(CardType cardType) {
//        int result = 1;
//
//        switch (cardType) {
//            case CONTACT:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_MODE + TRANS_TYPE_CONTACT,
//                        EncryptModeType.ENCRYPT_MODE_CBC.type);
//                break;
//            case CONTACTLESS:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_MODE + TRANS_TYPE_CONTACTLESS,
//                        EncryptModeType.ENCRYPT_MODE_CBC.type);
//                break;
//            case MAGSTRIPE:
//                result = SPUtils.getInstance().getInt(TRANS_ENCRYPT_MODE + TRANS_TYPE_MAGSTRIPE,
//                        EncryptModeType.ENCRYPT_MODE_CBC.type);
//                break;
//            default:
//                break;
//        }
//
//        return result;
//    }
//
//    public static void setTransEncryptVector(CardType cardType, String vector) {
//        switch (cardType) {
//            case CONTACT:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_VECTOR + TRANS_TYPE_CONTACT, vector);
//                break;
//            case CONTACTLESS:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_VECTOR + TRANS_TYPE_CONTACTLESS, vector);
//                break;
//            case MAGSTRIPE:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_VECTOR + TRANS_TYPE_MAGSTRIPE, vector);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static String getTransEncryptVector(CardType cardType) {
//        String result = "0000000000000000";
//
//        switch (cardType) {
//            case CONTACT:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_VECTOR + TRANS_TYPE_CONTACT, "0000000000000000");
//                break;
//            case CONTACTLESS:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_VECTOR + TRANS_TYPE_CONTACTLESS, "0000000000000000");
//                break;
//            case MAGSTRIPE:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_VECTOR + TRANS_TYPE_MAGSTRIPE, "0000000000000000");
//                break;
//            default:
//                break;
//        }
//
//        return result;
//    }
//
//    public static void setTransEncryptTransArmorPosId(CardType cardType, String posId) {
//        switch (cardType) {
//            case CONTACT:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TRANS_ARMOR_POS_ID + TRANS_TYPE_CONTACT, posId);
//                break;
//            case CONTACTLESS:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TRANS_ARMOR_POS_ID + TRANS_TYPE_CONTACTLESS, posId);
//                break;
//            case MAGSTRIPE:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TRANS_ARMOR_POS_ID + TRANS_TYPE_MAGSTRIPE, posId);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static String getTransEncryptTransArmorPosId(CardType cardType) {
//        String result = "12345678";
//
//        switch (cardType) {
//            case CONTACT:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_TRANS_ARMOR_POS_ID + TRANS_TYPE_CONTACT, "12345678");
//                break;
//            case CONTACTLESS:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_TRANS_ARMOR_POS_ID + TRANS_TYPE_CONTACTLESS, "12345678");
//                break;
//            case MAGSTRIPE:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_TRANS_ARMOR_POS_ID + TRANS_TYPE_MAGSTRIPE, "12345678");
//                break;
//            default:
//                break;
//        }
//
//        return result;
//    }
//
//    public static void setTransEncryptTransArmorKeyId(CardType cardType, String keyId) {
//        switch (cardType) {
//            case CONTACT:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TRANS_ARMOR_KEY_ID + TRANS_TYPE_CONTACT, keyId);
//                break;
//            case CONTACTLESS:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TRANS_ARMOR_KEY_ID + TRANS_TYPE_CONTACTLESS, keyId);
//                break;
//            case MAGSTRIPE:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_TRANS_ARMOR_KEY_ID + TRANS_TYPE_MAGSTRIPE, keyId);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static String getTransEncryptTransArmorKeyId(CardType cardType) {
//        String result = "166257982464";
//
//        switch (cardType) {
//            case CONTACT:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_TRANS_ARMOR_KEY_ID + TRANS_TYPE_CONTACT,
//                        "166257982464");
//                break;
//            case CONTACTLESS:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_TRANS_ARMOR_KEY_ID + TRANS_TYPE_CONTACTLESS,
//                        "166257982464");
//                break;
//            case MAGSTRIPE:
//                result = SPUtils.getInstance().getString(TRANS_ENCRYPT_TRANS_ARMOR_KEY_ID + TRANS_TYPE_MAGSTRIPE,
//                        "166257982464");
//                break;
//            default:
//                break;
//        }
//
//        return result;
//    }
//
//    public static void setTransEncryptBase64(CardType cardType, boolean open) {
//        switch (cardType) {
//            case CONTACT:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_BASE64 + TRANS_TYPE_CONTACT, open);
//                break;
//            case CONTACTLESS:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_BASE64 + TRANS_TYPE_CONTACTLESS, open);
//                break;
//            case MAGSTRIPE:
//                SPUtils.getInstance().put(TRANS_ENCRYPT_BASE64 + TRANS_TYPE_MAGSTRIPE, open);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static boolean isTransEncryptBase64(CardType cardType) {
//        boolean result = false;
//
//        switch (cardType) {
//            case CONTACT:
//                result = SPUtils.getInstance().getBoolean(TRANS_ENCRYPT_BASE64 + TRANS_TYPE_CONTACT, true);
//                break;
//            case CONTACTLESS:
//                result = SPUtils.getInstance().getBoolean(TRANS_ENCRYPT_BASE64 + TRANS_TYPE_CONTACTLESS, true);
//                break;
//            case MAGSTRIPE:
//                result = SPUtils.getInstance().getBoolean(TRANS_ENCRYPT_BASE64 + TRANS_TYPE_MAGSTRIPE, true);
//                break;
//            default:
//                break;
//        }
//
//        return result;
//    }
//
//    public enum CardType {
//        CONTACT,
//        CONTACTLESS,
//        MAGSTRIPE
//    }
}
