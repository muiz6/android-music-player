package com.muiz6.musicplayer.musicservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;

class _MediaSessionCallback extends MediaSessionCompat.Callback {

	private final MediaBrowserServiceCompat _service;
	private final MediaSessionCompat _session;
	private final AudioManager _audioManager;
	private final AudioManager.OnAudioFocusChangeListener _audioFocusListener;
	private final MediaPlayer.OnCompletionListener _mediaCompletionListener;
	private final NotificationCompat.Builder _notifBuilder;
	private final MediaPlayer _player = new MediaPlayer();
	private PlaybackStateCompat.Builder _pbStateBuilder;
	private MediaMetadataCompat.Builder _metadataBuilder;
	private BroadcastReceiver _noisyReceiver;

	_MediaSessionCallback(MediaBrowserServiceCompat service,
			MediaSessionCompat session,
			AudioManager audioManager,
			NotificationCompat.Builder notifBuilder,
			AudioManager.OnAudioFocusChangeListener listener,
			MediaPlayer.OnCompletionListener mpListener) {
		_service = service;
		_session = session;
		_audioManager = audioManager;
		_audioFocusListener = listener;
		_mediaCompletionListener = mpListener;
		_notifBuilder = notifBuilder;
		AudioAttributes.Builder audioAttrBuilder = new AudioAttributes.Builder()
				.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
				.setUsage(AudioAttributes.USAGE_MEDIA);
		_player.setAudioAttributes(audioAttrBuilder.build());
		_player.setOnCompletionListener(_mediaCompletionListener);

		_pbStateBuilder = new PlaybackStateCompat.Builder()
				.setActions(PlaybackStateCompat.ACTION_PLAY
						| PlaybackStateCompat.ACTION_PLAY_PAUSE
						| PlaybackStateCompat.ACTION_STOP)
				.setState(PlaybackStateCompat.STATE_STOPPED, 0, 0);
		_metadataBuilder = new MediaMetadataCompat.Builder()
				.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "")
				.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, "")
				.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "");

		_session.setPlaybackState(_pbStateBuilder.build());
		_session.setMetadata(_metadataBuilder.build());

		_noisyReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
					_session.getController().getTransportControls().pause();
				}
			}
		};
	}

	@Override
	public void onPlayFromUri(Uri uri, Bundle extras) {
		super.onPlayFromUri(uri, extras);

		// service is also and audio focus change listener
		int result = _audioManager.requestAudioFocus(_audioFocusListener,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			_player.reset();
			try {
				_player.setDataSource(_service, uri);
				_player.prepare();
				_player.start();

				_pbStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1);
				_session.setPlaybackState(_pbStateBuilder.build());
				_session.setActive(true);

				IntentFilter intentFilter =
						new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
				_service.registerReceiver(_noisyReceiver, intentFilter);

				_notifBuilder.setContentText(uri.toString());
				NotificationManagerCompat.from(_service)
						.notify(_NotificationBuilder.MUSIC_NOTIFICATION_ID, _notifBuilder.build());
			}
			catch (java.io.IOException e) {
				Log.e("MusicService", "could not play this track!", e);
			}
		}
	}

	@Override
	public void onPlay() {
		super.onPlay();

		_player.pause();
		_pbStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1);
		_session.setPlaybackState(_pbStateBuilder.build());
		_session.setActive(true);
		// MediaControllerCompat controller = _session.getController();
		// MediaMetadataCompat mediaMetadata = controller.getMetadata();
		// MediaDescriptionCompat description = mediaMetadata.getDescription();
	}

	@Override
	public void onPause() {
		super.onPause();

		_player.pause();
		_audioManager.abandonAudioFocus(_audioFocusListener);
		_pbStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,0,0);
		_session.setPlaybackState(_pbStateBuilder.build());
		_service.unregisterReceiver(_noisyReceiver);
	}

	@Override
	public void onStop() {
		super.onStop();

		_pbStateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,0,0);
		_session.setPlaybackState(_pbStateBuilder.build());
		_session.setActive(false);

		_player.release();
		_audioManager.abandonAudioFocus(_audioFocusListener);
		_service.stopSelf();
	}
}
