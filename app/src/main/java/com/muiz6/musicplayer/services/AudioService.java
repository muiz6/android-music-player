package com.muiz6.musicplayer.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AudioService extends Service {

    public class MyBinder extends Binder {

        public AudioService getService() {
            return AudioService.this;
        }
    }

    private  IBinder mBinder;
    private Handler mHandler;
    private boolean mIsPlaying;
    MediaPlayer mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new MyBinder();
        mIsPlaying = false;
        mPlayer = new MediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // mPlayer.release();

        // end the service when application is closed
        stopSelf();
    }
}
