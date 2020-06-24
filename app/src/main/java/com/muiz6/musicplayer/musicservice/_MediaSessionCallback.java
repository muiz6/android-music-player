package com.muiz6.musicplayer.musicservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

class _MediaSessionCallback extends MediaSessionCompat.Callback {

	private final MediaBrowserServiceCompat _service;
	private final MediaSessionCompat _session;
	private final AudioManager _audioManager;
	private final NotificationCompat.Builder _notifBuilder;
	private SimpleExoPlayer _player;
	private PlaybackStateCompat.Builder _pbStateBuilder;
	private MediaMetadataCompat.Builder _metadataBuilder;
	private BroadcastReceiver _noisyReceiver;

	_MediaSessionCallback(MediaBrowserServiceCompat service,
			MediaSessionCompat session,
			AudioManager audioManager,
			NotificationCompat.Builder notifBuilder,
			AudioManager.OnAudioFocusChangeListener listener) {
		_service = service;
		_session = session;
		_audioManager = audioManager;
		_notifBuilder = notifBuilder;

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

		if (_player == null) {
			_player = new SimpleExoPlayer.Builder(_service).build();
		}
		_player.setPlayWhenReady(true);

		// exoplayer handles audio focus automatically
		AudioAttributes audioAttr = new AudioAttributes.Builder()
				.setUsage(C.USAGE_MEDIA)
				.setContentType(C.CONTENT_TYPE_MUSIC)
				.build();
		_player.setAudioAttributes(audioAttr, true);
		MediaSessionConnector connector = new MediaSessionConnector(_session);
		connector.setPlayer(_player);
		_pbStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1);
		_session.setPlaybackState(_pbStateBuilder.build());
		_session.setActive(true);

		DataSource.Factory dataSourceFactory =
				new DefaultDataSourceFactory(_service, "exoplayer-codelab");
		MediaSource source = new ProgressiveMediaSource.Factory(dataSourceFactory)
				.createMediaSource(uri);
		_player.prepare(source, true, true);

		IntentFilter intentFilter =
				new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		_service.registerReceiver(_noisyReceiver, intentFilter);

		_notifBuilder.setContentText(uri.toString());
		NotificationManagerCompat.from(_service)
				.notify(_NotificationBuilder.MUSIC_NOTIFICATION_ID, _notifBuilder.build());
	}

	@Override
	public void onPlay() {
		super.onPlay();

		// _player.pause();
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

		if (_player != null) {
			_player.release();
			_player = null;
		}
		// _player.pause();
		// _audioManager.abandonAudioFocus(_audioFocusListener);
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

		// _player.release();
		if (_player != null) {
			_player.release();
			_player = null;
		}
		_service.stopSelf();
	}
}
