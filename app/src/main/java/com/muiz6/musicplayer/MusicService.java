package com.muiz6.musicplayer;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.callbacks.MediaSessionCallback;
import com.muiz6.musicplayer.models.SongDataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// overriding onBind() will result in media browser not binding to service
public class MusicService extends MediaBrowserServiceCompat {

    private enum _LoopOptions {
        LOOP_OFF, LOOP_CURRENT, LOOP_ALL
    }

    private static final String _TAG = "Audio Service";
    private MediaSessionCompat _session;
    private PlaybackStateCompat.Builder _stateBuilder;
    private MediaPlayer _player;
    // private NotificationManagerCompat mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        _player = new MediaPlayer();
        _player.setAudioAttributes(new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build());

        // initializing media session
        _session = new MediaSessionCompat(this, _TAG);
        _stateBuilder = new PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PLAY_PAUSE
                | PlaybackStateCompat.ACTION_STOP)
            .setState(PlaybackStateCompat.STATE_STOPPED, 0, 0);

        // mMetadataBuilder = new MediaMetadataCompat.Builder();

        _session.setPlaybackState(_stateBuilder.build());
        // mSession.setMetadata(mMetadataBuilder.build());
        _session.setCallback(new MediaSessionCallback(this));

        setSessionToken(_session.getSessionToken());
    }

    // TODO: perform long running operation in bg
    // @Override
    // public int onStartCommand(Intent intent, int flags, int startId) {
    //
    //     // TODO: fix notification, add channel id
    //
    //     // NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "")
    //     //     .setSmallIcon(R.drawable.ic_musicplayerlogo)
    //     //     .setContentTitle("Music Player")
    //     //     .setContentText("Service is Running")
    //     //     .setOngoing(true)
    //     //     .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
    //     //     .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    //
    //     // id must be non zero
    //     // mNotificationManager.notify(AUDIO_SERVICE_ID, builder.build());
    //
    //     return START_NOT_STICKY;
    // }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
        int clientUid,
        @Nullable Bundle rootHints) {

        // let everyone connect ;)
        return new BrowserRoot(getString(R.string.app_name), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
        @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        ArrayList<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();

        // final Map<String,String> rootItems = new HashMap<>();
        // rootItems.put("","");

        // if accessing from root
        if (parentId.equals(getString(R.string.app_name))) {
            final String arr[] = {"All Songs", "Playlists", "Albums", "Artists", "Folders", "Genres"};

            for (int i = 0; i < arr.length; i++) {

                builder.setMediaId("" + i)
                    .setTitle(arr[i]);

                MediaBrowserCompat.MediaItem item = new MediaBrowserCompat.MediaItem(builder.build(),
                    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);

                items.add(item);
            }
        }
        else if(parentId.equals("0")) {
            ArrayList<SongDataModel> data = Repository.getInstance(this).getSongList().getValue();

            for (int i = 0; i < data.size(); i++) {
                builder.setMediaId(parentId + "_" + i)
                    .setTitle(data.get(i).getTitle());

                MediaBrowserCompat.MediaItem item = new MediaBrowserCompat.MediaItem(builder.build(),
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

                items.add(item);
            }
        }

        result.sendResult(items);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // end the service when application is closed
        // mNotificationManager.cancel(AUDIO_SERVICE_NOTIFICATION_ID);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // mNotificationManager.cancel(AUDIO_SERVICE_NOTIFICATION_ID);;
    }

    public MediaPlayer getMediaPlayer() {
        return _player;
    }

    public MediaSessionCompat getMediaSession() {
        return _session;
    }

    public PlaybackStateCompat.Builder getPlaybackStateBuilder() {
        return _stateBuilder;
    }
}
