package com.muiz6.musicplayer.musicservice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.muiz6.musicplayer.Constants;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.musicservice.mainui.nowplaying.NowPlayingActivity;

import java.util.ArrayList;
import java.util.List;

import static com.muiz6.musicplayer.App.CHANNEL_ID;

// overriding onBind() will result in media browser not binding to service
// TODO: perform long running operation in bg
public class MusicService extends MediaBrowserServiceCompat
        implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener {

    public static final int AUDIO_SERVICE_NOTIFICATION_ID = 1;

    private static final String _TAG = "MusicService";
    private final ArrayList<MediaBrowserCompat.MediaItem> _result;
    private final MediaDescriptionCompat.Builder _itemDescriptionBuilder;
    private AudioManager _audioManager;
    private MediaSessionCompat _session;
    private _AsyncFetchAllSongs _taskFetchAllSongs;
    private NotificationCompat.Builder _notifBuilder;

    public MusicService() {

        _itemDescriptionBuilder =  new MediaDescriptionCompat.Builder();
        _result =  new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // initializing media session
        _session = new MediaSessionCompat(this, _TAG);
        this.setSessionToken(_session.getSessionToken());
        _session.setCallback(new _MediaSessionCallback(this,
                _session, _audioManager, _notifBuilder, this, this));

        final Intent intent = new Intent(this, NowPlayingActivity.class);
        _session.setSessionActivity(PendingIntent.getActivity(this, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT));

        _taskFetchAllSongs = new _AsyncFetchAllSongs(this);

        // setup notification builder for media style notification
        _notifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        MediaControllerCompat controller = _session.getController();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_face_black_24dp);
        _notifBuilder
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(_session.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2)
                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver
                                .buildMediaButtonPendingIntent(this,
                                        PlaybackStateCompat.ACTION_STOP)))

                // Add the metadata for the currently playing track
                .setContentTitle("Title")
                .setContentText("Artist")
                .setSubText("Album")
                .setLargeIcon(bmp)

                // Enable launching the player by clicking the notification
                .setContentIntent(controller.getSessionActivity())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(R.drawable.ic_face_black_24dp)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))

                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_previous_black_24dp,
                        "skip to previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))

                // Add a pause button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_pause_black_24dp, "pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))

                .addAction(new NotificationCompat.Action(R.drawable.ic_skip_next_black_24dp,
                        "skip to next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManagerCompat.from(this).notify(AUDIO_SERVICE_NOTIFICATION_ID,
                _notifBuilder.build());
        this.startForeground(MusicService.AUDIO_SERVICE_NOTIFICATION_ID, _notifBuilder.build());
        return super.onStartCommand(intent, flags, startId);
    }

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

            new _AsyncFetchAllSongs(this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        _session.getController().getTransportControls().stop();
        this.stopForeground(true);
    }

    // belongs to AudioManager.OnAudioFocusChangeListener interface
    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            _session.getController().getTransportControls().pause();
            _audioManager.abandonAudioFocus(this);
        }
    }

    // belongs to MediaPlayer.OnCompletionListener Interface
    @Override
    public void onCompletion(MediaPlayer mp) {
        _session.getController().getTransportControls().pause();
    }

    // req by async task fetch
    public ArrayList<MediaBrowserCompat.MediaItem> getMediaItems() {
        return _result;
    }

    // req by async task fetch
    public MediaDescriptionCompat.Builder getItemDescriptionBuilder() {
        return _itemDescriptionBuilder;
    }
}
