package com.muiz6.musicplayer.misc;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.muiz6.musicplayer.MusicService;

/**
 * callback to use with session object inside MusicService class
 */
public class MusicServiceMediaSessionCallback extends MediaSessionCompat.Callback {

    private final MusicService _service;
    private final PlaybackStateCompat.Builder _stateBuilder;
    private final MediaSessionCompat _session;
    private final AudioManager _audioManager;

    public MusicServiceMediaSessionCallback(MusicService service) {
        _service = service;
        _stateBuilder = service.getPlaybackStateBuilder();
        _session = service.getMediaSession();
        _audioManager = service.getAudioManager();
    }

    @Override
    public void onPlayFromUri(Uri uri, Bundle extras) {
        super.onPlayFromUri(uri, extras);

        // service is also and audio focus change listener
        int result = _audioManager.requestAudioFocus(_service, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
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
    }

    @Override
    public void onPlay() {
        super.onPlay();

        // TODO: display notification here

        _service.getMediaPlayer().pause();
        _stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,0,1);
        _session.setPlaybackState(_stateBuilder.build());
        // MediaControllerCompat controller = _session.getController();
        // MediaMetadataCompat mediaMetadata = controller.getMetadata();
        // MediaDescriptionCompat description = mediaMetadata.getDescription();



    }

    @Override
    public void onPause() {
        super.onPause();

        _service.getMediaPlayer().pause();
        _audioManager.abandonAudioFocus(_service);
        _stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,0,0);
        _session.setPlaybackState(_stateBuilder.build());
    }

    @Override
    public void onStop() {
        super.onStop();

        _stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,0,0);
        _session.setPlaybackState(_stateBuilder.build());

        _service.releaseMediaPlayer();
        _audioManager.abandonAudioFocus(_service);
        // TODO: call stopSelf here
        _service.stopSelf();
    }
}
