package com.xc.apex.nre.customerdemo.view.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xc.apex.nre.customerdemo.R;
import com.xc.apex.nre.customerdemo.utils.ToastUtil;

public class BaseActivity extends AppCompatActivity {

    private static final long DOUBLE_BACK_PRESS_INTERVAL = 2000;
    private long backPressedTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + DOUBLE_BACK_PRESS_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            ToastUtil.showToast(this, getString(R.string.txt_exit_app_tips));
            backPressedTime = System.currentTimeMillis();
        }
    }
}
