package com.muiz6.musicplayer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.muiz6.musicplayer.R;

public class AudioService extends Service {

    public class MyBinder extends Binder {

        public AudioService getService() {
            return AudioService.this;
        }
    }

    private  IBinder mBinder;
    private boolean mIsPlaying;
    MediaPlayer mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new MyBinder();
        mIsPlaying = false;
        // mPlayer = new MediaPlayer();
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.ic_musicplayerlogo)
            .setContentTitle("Music Player")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());

        // end the service when application is closed
        mPlayer.release();
        stopSelf();
    }

    public void playAudio(Uri path) {
        mPlayer = MediaPlayer.create(this, path);
        mPlayer.start();
    }
}
