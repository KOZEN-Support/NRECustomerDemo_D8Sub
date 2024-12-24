package com.xc.apex.nre.customerdemo.view;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xc.apex.nre.customerdemo.R;
import com.xc.apex.nre.customerdemo.databinding.ActivityDiscountBinding;
import com.xc.apex.nre.customerdemo.usb.event.FinishEvent;
import com.xc.apex.nre.customerdemo.usb.command.ReceiverCommand;
import com.xc.apex.nre.customerdemo.view.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 优惠券界面
 */
public class DiscountInfoActivity extends BaseActivity {
    private static final String TAG = "DiscountInfoActivity";
    private ActivityDiscountBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_discount);
        EventBus.getDefault().register(this);

        Glide.with(this)
                .asGif()
                .load(R.drawable.gif_vip)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.ivJoinVip);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishEvent(FinishEvent event) {
        Log.d(TAG, "[EventBus]onFinishEvent command = " + event.getCommand());
        String command = event.getCommand();
        if (!command.equalsIgnoreCase(ReceiverCommand.START_ORDERING)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
