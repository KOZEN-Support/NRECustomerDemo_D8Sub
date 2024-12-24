package com.xc.apex.nre.customerdemo.view;

import static com.xc.apex.nre.customerdemo.NREConstant.REQUEST_CODE_QRCODE_PERMISSIONS;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.xc.apex.nre.customerdemo.R;
import com.xc.apex.nre.customerdemo.databinding.ActivityCarousel2Binding;
import com.xc.apex.nre.customerdemo.databinding.ActivityCarouselAdvBinding;
import com.xc.apex.nre.customerdemo.model.MediaItemData;
import com.xc.apex.nre.customerdemo.utils.ToastUtil;
import com.xc.apex.nre.customerdemo.view.adapter.MediaViewPagerAdapter;
import com.xc.apex.nre.customerdemo.view.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播广告界面
 */
public class CarouselAdvActivity extends BaseActivity implements MediaViewPagerAdapter.changeNextPageListener {
    private static final String TAG = "CustomerMain";

//    private ActivityCarouselAdvBinding binding;
//    private MediaViewPagerAdapter mediaAdapter;
//    List<MediaItemData> mediaList = new ArrayList<>();

    private ActivityCarousel2Binding binding;
    private ExoPlayer exoPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_carousel2);

        // TODO 轮播 START ----->
        // 加载轮播资源
//        initCarouselSrc();
//        mediaAdapter = new MediaViewPagerAdapter(this, mediaList, this::onNextPage);
//        binding.viewPager.setAdapter(mediaAdapter);
        // 禁止手动滑动
//        binding.viewPager.setUserInputEnabled(false);
//        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            private RecyclerView recyclerView;
//            private int previousPosition = -1;
//
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                Log.d(TAG, "onPageSelected+ " + position);
//
//                if (recyclerView == null) {
//                    // 获取 ViewPager2 内部的 RecyclerView
//                    recyclerView = (RecyclerView) binding.viewPager.getChildAt(0);
//                }
//
//                // 停止之前页面的视频
//                if (previousPosition != -1) {
//                    releaseViewPager(recyclerView, previousPosition);
//                }
//
//                // 播放当前页面的视频
//                resumeViewPager(recyclerView, position);
//
//                // 更新上一位置
//                previousPosition = position;
//                Log.d(TAG, "onPageSelected- " + previousPosition);
//            }
//        });
        // TODO 轮播 END ----->

        // 播放视频
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(CarouselAdvActivity.this).build();
            binding.playerView.setPlayer(exoPlayer);
        }
        // 禁用控制器
        binding.playerView.setUseController(false);
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(getRawVideoUri(R.raw.video_xc_c8_avd))));
        exoPlayer.prepare();
        exoPlayer.play();
        // 设置为循环播放模式
        exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);

        checkPermissionAndCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (exoPlayer != null && !exoPlayer.isPlaying()) {
            exoPlayer.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

//    private void initCarouselSrc() {
//        mediaList.add(new MediaItemData(MediaItemData.TYPE_VIDEO, getRawVideoUri(R.raw.video_xc_c8_avd)));
//        mediaList.add(new MediaItemData(MediaItemData.TYPE_VIDEO, getRawVideoUri(R.raw.video_adv2)));
//        mediaList.add(new MediaItemData(MediaItemData.TYPE_IMAGE, R.drawable.img_adv1));
//    }

    private String getRawVideoUri(int rawResId) {
        return "android.resource://" + getPackageName() + "/" + rawResId;
    }

    @Override
    public void onNextPage(int pos) {
//        int totalSize = mediaAdapter.getItemCount();
//        int currentItem = (pos == totalSize - 1) ? 0 : pos + 1;
//        Log.d(TAG, "onNextPage===>" + currentItem);
//        binding.viewPager.setCurrentItem(currentItem, true);
//        binding.viewPager.getAdapter().notifyItemChanged(currentItem);
    }

//    private void releaseViewPager(RecyclerView recyclerView, int position) {
//        if (recyclerView == null || position >= mediaList.size()) {
//            return;
//        }
//        RecyclerView.ViewHolder previousHolder = recyclerView.findViewHolderForAdapterPosition(position);
//        if (previousHolder instanceof MediaViewPagerAdapter.MediaPagerViewHolder) {
//            ((MediaViewPagerAdapter.MediaPagerViewHolder) previousHolder).release();
//        }
//    }
//
//    private void resumeViewPager(RecyclerView recyclerView, int position) {
//        if (recyclerView == null || position >= mediaList.size()) {
//            return;
//        }
//        RecyclerView.ViewHolder currentHolder = recyclerView.findViewHolderForAdapterPosition(position);
//        if (currentHolder instanceof MediaViewPagerAdapter.MediaPagerViewHolder) {
//            Log.d(TAG, "resumeViewPager:: " + position);
//            MediaViewPagerAdapter.MediaPagerViewHolder videoHolder = (MediaViewPagerAdapter.MediaPagerViewHolder) currentHolder;
//            videoHolder.resume(mediaList.get(position), position);
//        }
//    }

    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_QRCODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_QRCODE_PERMISSIONS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //用户拒绝权限
                Log.e(TAG, "The user has refused camera permission.");
                ToastUtil.showToast(this, getString(R.string.txt_permission_failed));
            }
        }
    }

    public void release() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}