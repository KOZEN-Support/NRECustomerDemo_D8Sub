package com.xc.apex.nre.lib_payment.utils;

public class Utility {

    public static String formatCard(String card, boolean hidden) {
        if (card == null) {
            return "";
        }
        if (card.length() < 13 || card.length() > 20) {
            return card;
        }
        String number;
        String cardF = card.substring(0, 6);
        String cardB = card.substring(card.length() - 4);
        String padding = "*********";
        if (hidden) {
            number = cardF + padding + cardB;
        } else {
            number = card;
        }
        return number;
    }

    public static String formatAmount(String amount) {
        int flag = 0;
        String value = String.valueOf(Long.parseLong(amount));
        if (value.charAt(0) == '-') {
            flag = 1;
            value = value.substring(1);
        }
        StringBuilder builder = new StringBuilder();
        if (value.length() == 1) {
            builder.append("0.0").append(value);
        } else if (value.length() == 2) {
            builder.append("0.").append(value);
        } else {
            String data = value.substring(0, value.length() - 2);
            for (int i = 1; i <= data.length(); i++) {
                if ((i - 1) % 3 == 0 && i != 1) {
                    builder.append(",");
                }
                builder.append(data.substring(data.length() - i, data.length() - i + 1));
            }
            builder.reverse().append(".").append(value.substring(value.length() - 2));
        }
        if (flag == 1) {
            return "-" + builder.toString();
        } else {
            return builder.toString();
        }
    }
}
