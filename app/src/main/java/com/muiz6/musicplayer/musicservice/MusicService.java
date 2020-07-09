package com.muiz6.musicplayer.musicservice;

import android.app.PendingIntent;
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
import com.muiz6.musicplayer.musicprovider.MusicProvider;
import com.muiz6.musicplayer.notification.DescriptionAdapter;
import com.muiz6.musicplayer.notification.MusicNotificationManager;
import com.muiz6.musicplayer.ui.nowplaying.NowPlayingActivity;

import java.util.List;

// overriding onBind() will result in media browser not binding to service
public class MusicService extends MediaBrowserServiceCompat
	implements MediaSessionConnector.PlaybackPreparer {

	private static final String _TAG = "MusicService";
	private final MusicProvider _musicProvider;
	private MediaSessionCompat _session;
	private MediaSessionConnector _sessionConnector;
	private SimpleExoPlayer _player;
	private PlayerNotificationManager _notificationMgr;
	private _NoisyReceiver _noisyReceiver;

	public MusicService() {

		_musicProvider = MusicProvider.getInstance(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		// initializing media session
		_session = new MediaSessionCompat(this, _TAG);
		this.setSessionToken(_session.getSessionToken());
		// _session.setCallback(new _MediaSessionCallback(this,
		// 		_session, _musicProvider));

		// needed to connect media session with exoplayer
		_sessionConnector = new MediaSessionConnector(_session);
		_sessionConnector.setPlaybackPreparer(this);
		_sessionConnector.setQueueNavigator(_musicProvider.getQueueNavigator(_session));

		// session activity needed for notification click action
		// todo: look into request codes
		final Intent intent = new Intent(this, NowPlayingActivity.class);
		_session.setSessionActivity(PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT));

		_noisyReceiver = new _NoisyReceiver(_session.getController());

		_notificationMgr = new MusicNotificationManager(this,
				new DescriptionAdapter(_session.getController()),
				null);
		_notificationMgr.setMediaSessionToken(_session.getSessionToken());
	}

	// @Override
	// public int onStartCommand(Intent intent, int flags, int startId) {
	//
	// 	// docs say only required below ver 5.0
	// 	// but its required for notification buttons to work
	// 	// MediaButtonReceiver.handleIntent(_session, intent);
	// 	return super.onStartCommand(intent, flags, startId);
	// }

	@Nullable
	@Override
	public BrowserRoot onGetRoot(@NonNull String clientPackageName,
			int clientUid,
			@Nullable Bundle rootHints) {

		// let everyone connect ;)
		return MusicProvider.getBrowserRoot();
	}

	@Override
	public void onLoadChildren(@NonNull String parentId,
			@NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
		result.sendResult(_musicProvider.getChildren(parentId));
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
		_session.getController().getTransportControls().stop();
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
		MediaBrowserCompat.MediaItem mediaItem = _musicProvider.getMediaItemById(mediaId);
		if (mediaItem != null) {
			_session.setActive(true);

			if (_player == null) {
				_player = new SimpleExoPlayer.Builder(MusicService.this).build();
			}

			// exoplayer handles audio focus itself when attributes are set
			final AudioAttributes audioAttr = new AudioAttributes.Builder()
					.setUsage(C.USAGE_MEDIA)
					.setContentType(C.CONTENT_TYPE_MUSIC)
					.build();
			_player.setAudioAttributes(audioAttr, true);

			_sessionConnector.setPlayer(_player);
			_notificationMgr.setPlayer(_player);

			final MediaSource[] sources = _musicProvider.getQueueBytMediaId(mediaId);
			if (sources != null) {
				_player.prepare(new ConcatenatingMediaSource(sources));
				_player.seekTo(MusicProvider.getIndexFromMediaId(mediaId), 0);
				_player.setPlayWhenReady(playWhenReady);

				final IntentFilter intentFilter =
						new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
				MusicService.this.registerReceiver(_noisyReceiver, intentFilter);
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

	//
	//
	//

}
