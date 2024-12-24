package com.xc.apex.nre.customerdemo.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.xc.apex.nre.customerdemo.NRECustomerService;
import com.xc.apex.nre.customerdemo.R;
import com.xc.apex.nre.customerdemo.view.base.BaseActivity;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {
    private static final long SPLASH_DELAY = 4000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 预留启动服务的入口
        Intent serviceIntent = new Intent(this, NRECustomerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, CarouselAdvActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}
