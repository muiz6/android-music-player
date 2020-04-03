package com.muiz6.musicplayer.callbacks;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.muiz6.musicplayer.MusicService;

public class MusicServiceMediaSessionCallback extends MediaSessionCompat.Callback {

    private final MusicService _service;
    private final PlaybackStateCompat.Builder _stateBuilder;
    private final MediaSessionCompat _session;

    public MusicServiceMediaSessionCallback(MusicService service) {
        _service = service;
        _stateBuilder = service.getPlaybackStateBuilder();
        _session = service.getMediaSession();
    }

    @Override
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        super.onPlayFromMediaId(mediaId, extras);
    }

    @Override
    public void onPlayFromUri(Uri uri, Bundle extras) {
        super.onPlayFromUri(uri, extras);

        MediaPlayer player = _service.getMediaPlayer();
        player.reset();
        try {
            player.setDataSource(_service, uri);
            player.prepare();
            player.start();

            _stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,0,1);
            _session.setPlaybackState(_stateBuilder.build());
        }
        catch (java.io.IOException e) {
            Log.e("MusicService", "could not play this track!", e);
        }
    }

    @Override
    public void onPlay() {
        super.onPlay();

        // TODO: call startservice here
        // display notification here

        _service.getMediaPlayer().pause();
        _stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,0,1);
        _session.setPlaybackState(_stateBuilder.build());
        // MediaControllerCompat controller = _session.getController();
        // MediaMetadataCompat mediaMetadata = controller.getMetadata();
        // MediaDescriptionCompat description = mediaMetadata.getDescription();

        // NotificationCompat.Builder builder = new NotificationCompat.Builder(_service, CHANNEL_ID);
        //
        // builder
        //     // Add the metadata for the currently playing track
        //     .setContentTitle(description.getTitle())
        //     // .setContentText(description.getSubtitle())
        //     // .setSubText(description.getDescription())
        //     // .setLargeIcon(description.getIconBitmap())
        //
        //     // Enable launching the player by clicking the notification
        //     .setContentIntent(controller.getSessionActivity())
        //
        //     // Stop the service when the notification is swiped away
        //     .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(_service,
        //             PlaybackStateCompat.ACTION_STOP))
        //
        //     // Make the transport controls visible on the lockscreen
        //     .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        //
        //     // Add an app icon and set its accent color
        //     // Be careful about the color
        //     .setSmallIcon(R.drawable.ic_musicplayerlogo)
        //     // .setColor(ContextCompat.getColor(context, R.color.primaryDark))
        //
        //     // Add a pause button
        //     .addAction(new NotificationCompat.Action(
        //             R.drawable.ic_pause_black_24dp, "pause",
        //             MediaButtonReceiver.buildMediaButtonPendingIntent(_service,
        //                     PlaybackStateCompat.ACTION_PLAY_PAUSE)))
        //
        //     // Take advantage of MediaStyle features
        //     .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
        //             .setMediaSession(_session.getSessionToken())
        //             .setShowActionsInCompactView(0)
        //
        //             // Add a cancel button
        //             .setShowCancelButton(true)
        //             .setCancelButtonIntent(MediaButtonReceiver
        //                 .buildMediaButtonPendingIntent(_service, PlaybackStateCompat.ACTION_STOP)));
        //
        // // Display the notification and place the service in the foreground
        // _service.startForeground(AUDIO_SERVICE_NOTIFICATION_ID, builder.build());

    }

    @Override
    public void onPause() {
        super.onPause();

        _service.getMediaPlayer().pause();
        _stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,0,0);
        _session.setPlaybackState(_stateBuilder.build());
    }

    @Override
    public void onStop() {
        super.onStop();

        _stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,0,0);
        _session.setPlaybackState(_stateBuilder.build());

        _service.getMediaPlayer().release();

        // TODO: call stopSelf here
        _service.stopSelf();
    }
}
