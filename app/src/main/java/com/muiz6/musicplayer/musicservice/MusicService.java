package com.muiz6.musicplayer.musicservice;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.MyApp;
import com.muiz6.musicplayer.data.MusicRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

// overriding onBind() will result in media browser not binding to service
public class MusicService extends MediaBrowserServiceCompat
		implements MediaSessionConnector.PlaybackPreparer {

	@Inject MusicRepository musicRepository;
	@Inject PlayerNotificationManager notificationMgr;
	@Inject MediaSessionConnector sessionConnector;
	@Inject MediaSessionCompat session;
	@Inject MediaSessionConnector.QueueNavigator queueNavigator;
	@Inject
	@Named("NoisyReceiver")
	BroadcastReceiver noisyReceiver;

	private SimpleExoPlayer _player;

	@Override
	public void onCreate() {
		((MyApp) getApplication()).getAppComponent().getMusicServiceComponent().create()
				.inject(this);
		super.onCreate();

		// connect service to media session
		setSessionToken(session.getSessionToken());

		// connect media session with exoplayer
		sessionConnector.setPlaybackPreparer(this);
		sessionConnector.setQueueNavigator(musicRepository.getQueueNavigator(session));
		// sessionConnector.setQueueNavigator(queueNavigator);

		// to stop playback when headphones are removed
		noisyReceiver = new NoisyReceiver(session.getController());

		// connect media session to exoplayer notification manager
		notificationMgr.setMediaSessionToken(session.getSessionToken());
	}

	@Nullable
	@Override
	public BrowserRoot onGetRoot(@NonNull String clientPackageName,
			int clientUid,
			@Nullable Bundle rootHints) {

		// let everyone connect ;)
		return MusicRepository.getBrowserRoot();
	}

	@Override
	public void onLoadChildren(@NonNull String parentId,
			@NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
		musicRepository.sendResult(parentId, result);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);

		// end the service when application is closed
		stopSelf();
	}

	@Override
	public void onDestroy() {

		// stop playback and related processes
		session.getController().getTransportControls().stop();
		NotificationManagerCompat.from(this).cancelAll();
		super.onDestroy();
	}

	// the following methods
	// belong to
	// MediaSessionConnector.PlaybackPreparer

	@Override
	public long getSupportedPrepareActions() {
		return PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
	}

	@Override
	public void onPrepare(boolean playWhenReady) {}

	@Override
	public void onPrepareFromMediaId(@NonNull String mediaId,
			boolean playWhenReady,
			@Nullable Bundle extras) {
		MediaBrowserCompat.MediaItem mediaItem = musicRepository.getMediaItemById(mediaId);
		if (mediaItem != null) {
			session.setActive(true);

			if (_player == null) {
				_player = new SimpleExoPlayer.Builder(MusicService.this).build();
			}

			// exoplayer handles audio focus itself when attributes are set
			final AudioAttributes audioAttr = new AudioAttributes.Builder()
					.setUsage(C.USAGE_MEDIA)
					.setContentType(C.CONTENT_TYPE_MUSIC)
					.build();
			_player.setAudioAttributes(audioAttr, true);

			sessionConnector.setPlayer(_player);
			notificationMgr.setPlayer(_player);

			final MediaSource[] sources = musicRepository.getQueueBytMediaId(mediaId);
			if (sources != null) {
				_player.prepare(new ConcatenatingMediaSource(sources));
				_player.seekTo(MusicRepository.getIndexFromMediaId(mediaId), 0);
				_player.setPlayWhenReady(playWhenReady);

				final IntentFilter intentFilter =
						new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
				MusicService.this.registerReceiver(noisyReceiver, intentFilter);
			}
		}
	}

	@Override
	public void onPrepareFromSearch(@NonNull String query,
			boolean playWhenReady,
			@Nullable Bundle extras) {}

	@Override
	public void onPrepareFromUri(@NonNull Uri uri,
			boolean playWhenReady,
			@Nullable Bundle extras) {}

	@Override
	public boolean onCommand(@NonNull Player player,
			@NonNull ControlDispatcher controlDispatcher,
			@NonNull String command,
			@Nullable Bundle extras,
			@Nullable ResultReceiver cb) {
		return false;
	}
}
