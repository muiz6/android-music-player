package com.muiz6.musicplayer.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.models.SongDataModel;

import java.util.ArrayList;
import java.util.List;

import static com.muiz6.musicplayer.App.AUDIO_SERVICE_ID;

// media session
public class AudioService extends MediaBrowserService {

    private static final String TAG = "Audio Service";

    public class MyBinder extends Binder {

        public AudioService getService() {
            return AudioService.this;
        }
    }

    private class UpdateThread implements Runnable {

        @Override
        public void run() {
            while(true)
            {
                if (mPlayer != null && mPlayer.isPlaying()){
                    int value = (int) ((float) mPlayer.getCurrentPosition() / (float) mPlayer.getDuration() * 100);
                    // mProgressPercentage.setValue(value);

                    // used instead of setValue
                    // to communicate b/w background and main thread
                    mProgressPercentage.postValue(value);
                    Log.d(TAG, "progress: "+ value);
                }
                try {
                    mThread.sleep(2000);
                }
                catch (InterruptedException e) {}
            }
        }
    }

    private enum LoopOptions {
        LOOP_OFF, LOOP_CURRENT, LOOP_ALL
    }

    private  IBinder mBinder;
    private MediaPlayer mPlayer;
    private NotificationManagerCompat mNotificationManager;
    private ArrayList<SongDataModel> mPlayList;
    private boolean mShuffleOn;
    private LoopOptions mLoopValue;
    private Thread mThread;
    private MutableLiveData<Integer> mProgressPercentage;
    private MutableLiveData<Boolean> mIsAudioPlaying;
    private MutableLiveData<SongDataModel> mCurrentSong;

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new MyBinder();
        mNotificationManager = NotificationManagerCompat.from(this);
        mLoopValue = LoopOptions.LOOP_OFF;
        // mIsPlaying = false;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioAttributes(new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build());

        // mPlayer.;

        mProgressPercentage = new MutableLiveData<>();
        mProgressPercentage.setValue(0);

        mIsAudioPlaying = new MutableLiveData<>();
        mIsAudioPlaying.setValue(mPlayer.isPlaying());

        mCurrentSong = new MutableLiveData<>();
    }

    // TODO: perform long running operation in bg
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO: fix notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.ic_musicplayerlogo)
            .setContentTitle("Music Player")
            .setContentText("Service is Running")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // id must be non zero
        mNotificationManager.notify(AUDIO_SERVICE_ID, builder.build());

        // update progress info
        mThread = new Thread(new UpdateThread());
        mThread.start();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowser.MediaItem>> result) {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // mPlayer.release();

        // end the service when application is closed
        if (mPlayer != null) mPlayer.release();
        mNotificationManager.cancel(AUDIO_SERVICE_ID);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mNotificationManager.cancel(AUDIO_SERVICE_ID);;
    }

    public void playAudio(SongDataModel data) {
        mPlayer.reset();
        try {
            mPlayer.setDataSource(this, Uri.parse(data.getPath()));
            mPlayer.prepare();
            mPlayer.start();
            mCurrentSong.setValue(data);
            mIsAudioPlaying.setValue(true);
        }
        catch (java.io.IOException e) {
            Toast.makeText(this,
            "Invalid Path: Could not play this track!",
                Toast.LENGTH_SHORT).show();
        } // do nothing and terminate call if url is invalid
    }

    // TODO: implement this method
    public void playAudioMultiple(ArrayList<SongDataModel> data) {

    }

    public void playAudioMultiple(ArrayList<SongDataModel> data, boolean shuffleOn) {
        mShuffleOn = shuffleOn;
        playAudioMultiple(data);
    }

    public ArrayList<SongDataModel> getPlayList() {
        return null;
    }

    public LiveData<Integer> getProgressPercentage() {
        return mProgressPercentage;
    }

    public LiveData<Boolean> getPlayState() {
        return mIsAudioPlaying;
    }

    public LiveData<SongDataModel> getCurrentSong() {
        return mCurrentSong;
    }
}
