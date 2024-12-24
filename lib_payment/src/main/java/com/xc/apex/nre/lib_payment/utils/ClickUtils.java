package com.xc.apex.nre.lib_payment.utils;

/**
 * 点击工具类。
 */
public class ClickUtils {
    // 私有构造函数，防止外部实例化
    private ClickUtils() {
    }

    private int clickCount = 0;
    private long lastClickTime = 0;

    /**
     * 多次点击触发回调。
     *
     * @param callback 回调函数
     * @param count    点击次数
     * @param timeout  时间间隔（毫秒）
     */
    public void clickTrigger(Runnable callback, int count, long timeout) {
        long currentTime = System.currentTimeMillis();
        // 如果当前时间与上次点击时间间隔大于超时时间，重置点击次数
        if (currentTime - lastClickTime > timeout) {
            clickCount = 0;
        }
        clickCount++;
        lastClickTime = currentTime;
        // 当点击次数达到指定次数时，执行回调函数并重置点击次数
        if (clickCount == count) {
            callback.run();
            clickCount = 0;
        }
    }

    /**
     * 防抖处理。
     *
     * @param callback 回调函数
     * @param delay    延迟时间（毫秒）
     */
    public void debounce(Runnable callback, long delay) {
        long currentTime = System.currentTimeMillis();
        // 如果当前时间与上次点击时间间隔大于延迟时间，执行回调函数并更新上次点击时间
        if (currentTime - lastClickTime > delay) {
            callback.run();
            lastClickTime = currentTime;
        }
    }

    /**
     * 获取单例实例。
     */
    public static ClickUtils getInstance() {
        if (instance == null) {
            instance = new ClickUtils();
        }
        return instance;
    }

    // 单例实例
    private static ClickUtils instance;

    // 示例使用
    public static void main(String[] args) {
        ClickUtils clickUtils = ClickUtils.getInstance();

        // 在2秒内连续点击3次后触发回调函数
        clickUtils.clickTrigger(() -> {
            System.out.println("连续点击3次触发的回调函数");
        }, 3, 2000);
    }
}