package com.xc.apex.nre.lib_payment.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public final class AppUtils {

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(final Context context) {
        if (context == null) {
            return;
        }
        init((Application) context.getApplicationContext());
    }

    public static void init(final Application app) {
        if (sApplication == null) {
            sApplication = app;
        } else {
            if (app != null && app.getClass() != sApplication.getClass()) {
                sApplication = app;
            }
        }
    }

    public static Application getApp() {
        if (sApplication != null) {
            return sApplication;
        }
        return null;
    }

    public static String getAppVersionName() {
        return getAppVersionName(AppUtils.getApp().getPackageName());
    }

    public static String getAppVersionName(final String packageName) {
        if (isSpace(packageName)) {
            return "";
        }
        try {
            PackageManager pm = AppUtils.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
