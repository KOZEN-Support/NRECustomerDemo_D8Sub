package com.xc.apex.nre.lib_payment.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.xc.apex.nre.lib_payment.R;

import java.util.concurrent.CountDownLatch;

public class AudioBeep implements SoundPool.OnLoadCompleteListener {
    private static final String TAG = "AudioBeep";

    private static final float BEEP_VOLUME = 1.00f;
    private int mSoundID = 0;
    private int mSoundID2 = 0;
    private SoundPool mSoundPool = null;

    private final CountDownLatch mCound = new CountDownLatch(1);
    private boolean mMark;

    private final Context mContext;

    public AudioBeep(Context ctx) {
        Log.d(TAG, "create");

        mContext = ctx;

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_LOW_LATENCY | AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .build();

        this.mSoundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attributes)
                .build();

        this.mSoundPool.setOnLoadCompleteListener(this);
        this.mSoundID = this.mSoundPool.load(mContext, R.raw.success2, 1);
        this.mSoundID2 = this.mSoundPool.load(mContext, R.raw.failed, 1);
    }

    public void play(boolean isSuccess) {
        try {
            while (!this.mMark) {
                this.mCound.await();
            }
            int resID = isSuccess ? mSoundID : mSoundID2;
            this.mSoundPool.play(resID, BEEP_VOLUME, BEEP_VOLUME, 1, 0, 1.0f);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        Log.d(TAG, "close");
        this.mSoundPool.autoPause();
        this.mSoundPool.unload(this.mSoundID);
        this.mSoundPool.release();
        this.mSoundPool = null;
        this.mSoundID = 0;
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.d(TAG, "onLoadComplete sampleId = " + sampleId + ", status = " + status);
        this.mMark = true;
        this.mCound.countDown();
    }
}
