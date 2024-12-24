package com.xc.apex.nre.customerdemo.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xc.apex.nre.customerdemo.R;
import com.xc.apex.nre.customerdemo.model.MediaItemData;

import java.util.List;

public class MediaViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MediaViewPagerAdapter";
    private static final int DEF_DISPLAY_DURATION = 5000;

    private static Context context;
    private final List<MediaItemData> mediaList;
    private static changeNextPageListener changeNextPageListener;

    public MediaViewPagerAdapter(Context context, List<MediaItemData> mediaList, changeNextPageListener listener) {
        this.context = context;
        this.mediaList = mediaList;
        this.changeNextPageListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder+");
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_media_pager, parent, false);
        return new MediaPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder:: " + position);
        ((MediaPagerViewHolder) holder).bind(mediaList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class MediaPagerViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout pagerLayout;
        private ImageView imageView;
        private PlayerView playerView;

        private int curPosition = -1;
        private ExoPlayer exoPlayer;
        private Handler handler = new Handler();
        Runnable imgDisappearRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "image imgDisappearRunnable:: " + curPosition);
                if (changeNextPageListener != null && curPosition >= 0) {
                    changeNextPageListener.onNextPage(curPosition);
                }
            }
        };

        MediaPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerLayout = itemView.findViewById(R.id.layout_pager);
            imageView = itemView.findViewById(R.id.imageView);
            playerView = itemView.findViewById(R.id.playerView);
        }

        public void bind(MediaItemData data, int pos) {
            Log.d(TAG, "MediaPagerViewHolder bind:: " + pos);
            resume(data, pos);
        }

        public void release() {
            Log.d(TAG, "MediaPagerViewHolder release:: " + curPosition);
            if (exoPlayer != null) {
                exoPlayer.stop();
                exoPlayer.release();
                exoPlayer = null;
            }
            if (handler != null && handler.hasCallbacks(imgDisappearRunnable)) {
                handler.removeCallbacks(imgDisappearRunnable);
            }
        }

        public void resume(MediaItemData item, int pos) {
//            if (curPosition == pos) {
//                Log.e(TAG, "The ViewPager has already been loaded.");
//                return;
//            }

            curPosition = pos;
            Log.e(TAG, "MediaPagerViewHolder resume:: curPosition = " + pos + " , type = " + item.getType());

            if (item.getType() == MediaItemData.TYPE_VIDEO) {
                // 刷新item显示
                playerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                // 加载视频播放器资源
                if (exoPlayer == null) {
                    exoPlayer = new ExoPlayer.Builder(playerView.getContext()).build();
                    playerView.setPlayer(exoPlayer);
                }
                // 禁用控制器
                playerView.setUseController(false);
                exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(item.getUrl())));
                exoPlayer.prepare();
                exoPlayer.play();
                // 监听播放状态
                exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_READY) {
                            Log.d(TAG, "STATE_READY+");
                        }

                        if (playbackState == Player.STATE_ENDED) {
                            if (changeNextPageListener != null) {
                                Log.d(TAG, "VIDEO OVER-");
                                changeNextPageListener.onNextPage(pos);
                            }
                        }
                    }
                });
            } else {
                // 刷新item显示
                playerView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                // 设置图片资源
                Glide.with(imageView.getContext()).load(item.getSrcId()).into(imageView);
                // 定时消失，显示下一个资源
                if (handler == null) {
                    handler = new Handler();
                }
                if (handler.hasCallbacks(imgDisappearRunnable)) {
                    handler.removeCallbacks(imgDisappearRunnable);
                }
                handler.postDelayed(imgDisappearRunnable, DEF_DISPLAY_DURATION);
            }
        }
    }

    public interface changeNextPageListener {
        void onNextPage(int pos);
    }
}
