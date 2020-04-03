package com.muiz6.musicplayer;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.media.MediaBrowserServiceCompat;

import com.muiz6.musicplayer.callbacks.MusicServiceMediaSessionCallback;
import com.muiz6.musicplayer.misc.MusicServiceAsyncTaskFetchAllSongs;
import com.muiz6.musicplayer.util.Constants;

import java.util.ArrayList;
import java.util.List;

// overriding onBind() will result in media browser not binding to service
public class MusicService extends MediaBrowserServiceCompat {

    private static final String _TAG = "MusicService";
    private final ArrayList<MediaBrowserCompat.MediaItem> _result;
    private final MediaDescriptionCompat.Builder _itemDescriptionBuilder;
    private MediaSessionCompat _session;
    private PlaybackStateCompat.Builder _stateBuilder;
    private MediaPlayer _player;
    private MusicServiceAsyncTaskFetchAllSongs _taskFetchAllSongs;

    public MusicService() {
        super();

        _itemDescriptionBuilder =  new MediaDescriptionCompat.Builder();
        _result =  new ArrayList<>();

    }

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
        this.setSessionToken(_session.getSessionToken());
        _stateBuilder = new PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PLAY_PAUSE
                | PlaybackStateCompat.ACTION_STOP)
            .setState(PlaybackStateCompat.STATE_STOPPED, 0, 0);

        // mMetadataBuilder = new MediaMetadataCompat.Builder();

        _session.setPlaybackState(_stateBuilder.build());
        // mSession.setMetadata(mMetadataBuilder.build());
        _session.setCallback(new MusicServiceMediaSessionCallback(this));

        _taskFetchAllSongs = new MusicServiceAsyncTaskFetchAllSongs(this);
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
        return new BrowserRoot(Constants.MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
        @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        // if accessing from root
        if (parentId.equals(Constants.MEDIA_ID_ROOT)) {

            final ArrayList<Pair<String,String>> rootItems = new ArrayList<>();
            rootItems.add(new Pair<>(Constants.MEDIA_ID_ALL_SONGS, "All Songs"));

            for (int i = 0; i < rootItems.size(); i++) {
                final String id = rootItems.get(i).first;
                final String title = rootItems.get(i).second;
                final int flag;

                // all songs item is also playable while rest are only browsable
                if (id.equals(Constants.MEDIA_ID_ALL_SONGS)) {
                    flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                            | MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;
                }
                else {
                    flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE;
                }

                _itemDescriptionBuilder.setMediaId(id)
                        .setTitle(title)
                        .setSubtitle(title);
                MediaBrowserCompat.MediaItem item =
                        new MediaBrowserCompat.MediaItem(_itemDescriptionBuilder.build(), flag);
                _result.add(item);
            }
        }
        else if(parentId.equals(Constants.MEDIA_ID_ALL_SONGS)) {

            try {
                _taskFetchAllSongs.execute();
            }
            catch (Exception e) {
                // do nothing
            }

            new MusicServiceAsyncTaskFetchAllSongs(this);
        }

        result.sendResult(_result);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // end the service when application is closed
        // mNotificationManager.cancel(AUDIO_SERVICE_NOTIFICATION_ID);
        stopSelf();
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

    public ArrayList<MediaBrowserCompat.MediaItem> getMediaItems() {
        return _result;
    }

    public MediaDescriptionCompat.Builder getItemDescriptionBuilder() {
        return _itemDescriptionBuilder;
    }
}
