package com.xc.apex.nre.customerdemo.usb.command;

/**
 * 接收主屏的指令合集。指令统一已‘P’开头
 */
public class ReceiverCommand {

    // 启动副屏APK
    public static final String START_CUSTOMER_APK = "P000001";

    // 开始点单
    public static final String START_ORDERING = "P000002";

    // 现金买单：仅切换支付方式
    public static final String PAY_BY_CASH = "P000003";

    // 卡支付：仅切换支付方式
    public static final String PAY_BY_CARD = "P000004";

    // 扫码支付：仅切换支付方式
    public static final String PAY_BY_QRCODE = "P000005";

    // 取消买单
    public static final String CANCEL_CHARGE = "P000006";

    // 开始买单：发送订单（默认现金支付）
    public static final String START_CHARGE = "P000007";

    // 现金支付成功
    public static final String CASH_CHARGE_SUCCESS = "P000008";
}
