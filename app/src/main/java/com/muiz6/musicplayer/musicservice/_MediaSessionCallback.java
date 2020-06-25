package com.muiz6.musicplayer.musicservice;

import android.content.BroadcastReceiver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

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
import com.muiz6.musicplayer.musicservice.musicprovider.MusicProvider;

class _MediaSessionCallback extends MediaSessionCompat.Callback {

	private final MediaBrowserServiceCompat _service;
	private final MediaSessionCompat _session;
	// private final AudioManager _audioManager;
	private final NotificationCompat.Builder _notifBuilder;
	private final MediaSessionConnector _connector;
	private final MusicProvider _musicProvider;
	private final BroadcastReceiver _noisyReceiver;
	private SimpleExoPlayer _player;
	private MediaMetadataCompat.Builder _metadataBuilder;

	_MediaSessionCallback(MediaBrowserServiceCompat service,
			MediaSessionCompat session,
			AudioManager audioManager,
			NotificationCompat.Builder notifBuilder,
			MusicProvider musicProvider) {
		_service = service;
		_session = session;
		// _audioManager = audioManager;
		_notifBuilder = notifBuilder;
		_musicProvider = musicProvider;

		_metadataBuilder = new MediaMetadataCompat.Builder()
				.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "")
				.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, "")
				.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "");

		// _session.setPlaybackState(new PlaybackStateCompat.Builder()
		// 		.setActions(PlaybackStateCompat.ACTION_PLAY
		// 				| PlaybackStateCompat.ACTION_PLAY_PAUSE
		// 				| PlaybackStateCompat.ACTION_STOP)
		// 		.setState(PlaybackStateCompat.STATE_STOPPED, 0, 0)
		// 		.build());
		_session.setMetadata(_metadataBuilder.build());
		_connector = new MediaSessionConnector(_session);

		_noisyReceiver = new _NoisyReceiver(_session.getController());
	}

	@Override
	public void onPlay() {
		super.onPlay();

		// _player.pause();
		// _pbStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1);
		// _session.setPlaybackState(_pbStateBuilder.build());
		_session.setActive(true);
		// MediaControllerCompat controller = _session.getController();
		// MediaMetadataCompat mediaMetadata = controller.getMetadata();
		// MediaDescriptionCompat description = mediaMetadata.getDescription();
		// IntentFilter intentFilter =
		// 		new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		// _service.registerReceiver(_noisyReceiver, intentFilter);
	}

	@Override
	public void onPlayFromMediaId(String mediaId, Bundle extras) {
		super.onPlayFromMediaId(mediaId, extras);
		MediaBrowserCompat.MediaItem mediaItem = _musicProvider.getMediaItemById(mediaId);
		if (mediaItem != null) {
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

			_connector.setPlayer(_player);
			_session.setActive(true);

			DataSource.Factory dataSourceFactory =
					new DefaultDataSourceFactory(_service, "exoplayer-codelab");
			MediaSource source = new ProgressiveMediaSource.Factory(dataSourceFactory)
					.createMediaSource(mediaItem.getDescription().getMediaUri());
			_player.prepare(source, true, true);
			_notifBuilder.setContentTitle(mediaItem.getDescription().getTitle());
			_notifBuilder.setContentText(mediaItem.getDescription().getSubtitle());
			_notifBuilder.setSubText(mediaItem.getDescription().getDescription());
			NotificationManagerCompat.from(_service)
					.notify(_NotificationBuilder.MUSIC_NOTIFICATION_ID, _notifBuilder.build());
		}
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

		_connector.setPlayer(_player);
		_session.setActive(true);

		DataSource.Factory dataSourceFactory =
				new DefaultDataSourceFactory(_service, "exoplayer-codelab");
		MediaSource source = new ProgressiveMediaSource.Factory(dataSourceFactory)
				.createMediaSource(uri);
		_player.prepare(source, true, true);

		// IntentFilter intentFilter =
		// 		new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		// _service.registerReceiver(_noisyReceiver, intentFilter);

		_notifBuilder.setContentText(uri.toString());
		NotificationManagerCompat.from(_service)
				.notify(_NotificationBuilder.MUSIC_NOTIFICATION_ID, _notifBuilder.build());
	}

	@Override
	public void onPause() {
		super.onPause();

		_player.setPlayWhenReady(false);
		// if (_player != null) {
		// 	_player.release();
		// 	_player = null;
		// }
		// _player.pause();
		// _audioManager.abandonAudioFocus(_audioFocusListener);
		// _pbStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,0,0);
		// _session.setPlaybackState(_pbStateBuilder.build());

		// _service.unregisterReceiver(_noisyReceiver);
	}

	@Override
	public void onStop() {
		super.onStop();

		// _pbStateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,0,0);
		// _session.setPlaybackState(_pbStateBuilder.build());
		_session.setActive(false);

		// _player.release();
		if (_player != null) {
			_player.release();
			_player = null;
		}
		_service.stopSelf();
		// _service.unregisterReceiver(_noisyReceiver);
	}
}
