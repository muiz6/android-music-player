package com.muiz6.musicplayer.musicservice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.muiz6.musicplayer.musicservice.musicprovider.MusicProvider;
import com.muiz6.musicplayer.ui.nowplaying.NowPlayingActivity;

import java.util.List;

// overriding onBind() will result in media browser not binding to service
// TODO: perform long running operation in bg
public class MusicService extends MediaBrowserServiceCompat {

	private static final String _TAG = "MusicService";
	private final MusicProvider _musicProvider;
	private AudioManager _audioManager; // todo: use AudioManagerCompat instead
	private MediaSessionCompat _session;
	private _NotificationBuilder _notifBuilder;

	public MusicService() {

		_musicProvider = MusicProvider.getInstance(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		_audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		// initializing media session
		_session = new MediaSessionCompat(this, _TAG);
		this.setSessionToken(_session.getSessionToken());
		_notifBuilder = new _NotificationBuilder(this, _session);
		_session.setCallback(new _MediaSessionCallback(this,
				_session, _audioManager, _notifBuilder, _musicProvider));

		// session activity needed for notification click action
		final Intent intent = new Intent(this, NowPlayingActivity.class);
		_session.setSessionActivity(PendingIntent.getActivity(this, 1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT));

		// _taskFetchAllSongs = new _AsyncFetchAllSongs(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.startForeground(_NotificationBuilder.MUSIC_NOTIFICATION_ID, _notifBuilder.build());

		// docs say only required below ver 5.0
		// but its required for notification buttons to work
		MediaButtonReceiver.handleIntent(_session, intent);
		return super.onStartCommand(intent, flags, startId);
	}

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
		// mNotificationManager.cancel(AUDIO_SERVICE_NOTIFICATION_ID);
		stopSelf();
	}

	@Override
	public void onDestroy() {
		this.stopForeground(true);
		_session.getController().getTransportControls().stop();
		super.onDestroy();
	}

	// belongs to AudioManager.OnAudioFocusChangeListener interface
	// @Override
	// public void onAudioFocusChange(int focusChange) {
	// 	if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
	// 		_session.getController().getTransportControls().pause();
	// 		_audioManager.abandonAudioFocus(this);
	// 	}
	// }

	// // req by async task fetch
	// public ArrayList<MediaBrowserCompat.MediaItem> getMediaItems() {
	// 	return _result;
	// }
	//
	// // req by async task fetch
	// public MediaDescriptionCompat.Builder getItemDescriptionBuilder() {
	// 	return _itemDescriptionBuilder;
	// }
}
