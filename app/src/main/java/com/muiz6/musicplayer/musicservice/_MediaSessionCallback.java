package com.muiz6.musicplayer.musicservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.media.MediaBrowserServiceCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.muiz6.musicplayer.musicprovider.MusicProvider;
import com.muiz6.musicplayer.notification.DescriptionAdapter;
import com.muiz6.musicplayer.notification.MusicNotificationManager;
import com.muiz6.musicplayer.ui.nowplaying.NowPlayingActivity;

// TODO: perform long running operation in bg
class _MediaSessionCallback extends MediaSessionCompat.Callback
		implements PlayerNotificationManager.NotificationListener {

	private final MediaMetadataCompat.Builder _metadataBuilder = new MediaMetadataCompat.Builder();
	private final MediaBrowserServiceCompat _service;
	private final MediaSessionCompat _session;
	private final MediaSessionConnector _sessionConnector;
	private final MusicProvider _musicProvider;
	private final BroadcastReceiver _noisyReceiver;
	private final PlayerNotificationManager _notificationMgr;
	private SimpleExoPlayer _player;

	_MediaSessionCallback(MediaBrowserServiceCompat service,
			MediaSessionCompat session,
			MusicProvider musicProvider) {
		_service = service;
		_session = session;
		_musicProvider = musicProvider;

		// todo: look into request codes
		// set session activity for notification
		final Intent intent = new Intent(_service, NowPlayingActivity.class);
		_session.setSessionActivity(PendingIntent
				.getActivity(_service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

		_sessionConnector = new MediaSessionConnector(_session);
		_sessionConnector.setMediaMetadataProvider(_musicProvider);

		_noisyReceiver = new _NoisyReceiver(_session.getController());

		_notificationMgr = new MusicNotificationManager(_service,
				new DescriptionAdapter(_session.getController()), this);
		_notificationMgr.setMediaSessionToken(_session.getSessionToken());
	}

	@Override
	public void onPlay() {
		super.onPlay();

		_session.setActive(true);
		// MediaControllerCompat controller = _session.getController();
		// MediaMetadataCompat mediaMetadata = controller.getMetadata();
		// MediaDescriptionCompat description = mediaMetadata.getDescription();

		// register noisy receiver
		IntentFilter intentFilter =
				new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		_service.registerReceiver(_noisyReceiver, intentFilter);
		// NotificationManagerCompat.from(_service)
		// 		.notify(_NotificationManager.MUSIC_NOTIFICATION_ID, _notifBuilder.build());
	}

	@Override
	public void onPlayFromMediaId(String mediaId, Bundle extras) {
		super.onPlayFromMediaId(mediaId, extras);
		MediaBrowserCompat.MediaItem mediaItem = _musicProvider.getMediaItemById(mediaId);
		if (mediaItem != null) {
			_session.setActive(true);

			// final MediaDescriptionCompat description = mediaItem.getDescription();
			// CharSequence mediaTitle = description.getTitle();
			// CharSequence mediaArtist = description.getSubtitle();
			// CharSequence mediaAlbum = description.getDescription();
			// if (mediaTitle == null) {
			// 	mediaTitle = "Title";
			// }
			// if (mediaArtist == null) {
			// 	mediaArtist = "Unknown Artist";
			// }
			// if (mediaAlbum == null) {
			// 	mediaAlbum = "Unknown Album";
			// }
			// _session.setMetadata(_metadataBuilder
			// 		.putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaTitle.toString())
			// 		.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
			// 				mediaArtist.toString())
			// 		.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mediaAlbum.toString())
			// 		.build());

			if (_player == null) {
				_player = new SimpleExoPlayer.Builder(_service).build();
			}

			// exoplayer handles audio focus itself when attributes are set
			AudioAttributes audioAttr = new AudioAttributes.Builder()
					.setUsage(C.USAGE_MEDIA)
					.setContentType(C.CONTENT_TYPE_MUSIC)
					.build();
			_player.setAudioAttributes(audioAttr, true);

			_sessionConnector.setPlayer(_player);
			_notificationMgr.setPlayer(_player);

			final DataSource.Factory dataSourceFactory =
					new DefaultDataSourceFactory(_service, "exoplayer-codelab");
			// final MediaSource source = new ProgressiveMediaSource.Factory(dataSourceFactory)
			// 		.createMediaSource(mediaItem.getDescription().getMediaUri());
			final MediaSource[] sources =_musicProvider.getQueueBytMediaId(mediaId);
			if (sources != null) {
				_player.prepare(new ConcatenatingMediaSource(sources));
				// _player.prepare(source, true, true);
				_player.setPlayWhenReady(true);

				IntentFilter intentFilter =
						new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
				_service.registerReceiver(_noisyReceiver, intentFilter);
			}
			// NotificationManagerCompat.from(_service)
			// 		.notify(_NotificationManager.MUSIC_NOTIFICATION_ID, _notifBuilder.build());
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		_player.setPlayWhenReady(false);
		if (_player != null) {
			_player.release();
			_player = null;
		}

		_service.unregisterReceiver(_noisyReceiver);

		// _player.pause();
		// _audioManager.abandonAudioFocus(_audioFocusListener);
		// _pbStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,0,0);
		// _session.setPlaybackState(_pbStateBuilder.build());

		// NotificationManagerCompat.from(_service)
		// 		.notify(_NotificationManager.MUSIC_NOTIFICATION_ID, _notifBuilder.build());
	}

	@Override
	public void onStop() {
		super.onStop();

		// _pbStateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,0,0);
		// _session.setPlaybackState(_pbStateBuilder.build());
		_session.setActive(false);

		// remove notification
		_notificationMgr.setPlayer(null);

		// _player.release();
		if (_player != null) {
			_player.release();
			_player = null;
		}

		_service.stopSelf();
		// _service.unregisterReceiver(_noisyReceiver);
	}

	// Following methods
	// belong to
	// PlayerNotificationManager.NotificationListener

	@Override
	public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
		_session.getController().getTransportControls().stop();
	}

	@Override
	public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
		_service.startForeground(notificationId, notification);
	}
}
