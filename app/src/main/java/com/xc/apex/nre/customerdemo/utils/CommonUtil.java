package com.xc.apex.nre.customerdemo.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

public class CommonUtil {
    private static final String TAG = "CommonUtil";

    public static boolean isAppInForeground(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            String topPackageName = tasks.get(0).topActivity.getPackageName();
            return packageName.equals(topPackageName);
        }
        return false;
    }

    public static void launchApkByService(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        // 创建启动目标应用的 Intent
        Intent launchIntent = packageManager.getLaunchIntentForPackage(pkgName);
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 必须添加 FLAG_ACTIVITY_NEW_TASK
            context.startActivity(launchIntent);
        } else {
            // 目标应用未安装
            Log.e(TAG, "Target app is not installed.");
        }
    }

    public static boolean isTargetPageInForeground(Context context, String clsName) {
        android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
        if (runningTasks != null && runningTasks.size() > 0) {
            ComponentName comName = runningTasks.get(0).topActivity;
            return comName != null && clsName.equals(comName.getClassName());
        }
        return false;
    }

    public static void launchTargetPageByService(Context context, String pkgName, String clsName) {
        Intent intent = new Intent();
        intent.setClassName(pkgName, clsName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Target page failed to open.");
        }
    }

    public static void launchTargetPageByService(Context context, String pkgName, String clsName, String dataKey, String data) {
        Intent intent = new Intent();
        intent.setClassName(pkgName, clsName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(dataKey, data);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Target page failed to open.");
        }
    }
}
