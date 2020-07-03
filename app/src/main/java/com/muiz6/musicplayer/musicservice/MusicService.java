package com.muiz6.musicplayer.musicservice;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.muiz6.musicplayer.musicprovider.MusicProvider;
import com.muiz6.musicplayer.ui.nowplaying.NowPlayingActivity;

import java.util.List;

// overriding onBind() will result in media browser not binding to service
public class MusicService extends MediaBrowserServiceCompat {

	private static final String _TAG = "MusicService";
	private final MusicProvider _musicProvider;
	private MediaSessionCompat _session;

	public MusicService() {

		_musicProvider = MusicProvider.getInstance(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		// initializing media session
		_session = new MediaSessionCompat(this, _TAG);
		this.setSessionToken(_session.getSessionToken());
		_session.setCallback(new _MediaSessionCallback(this,
				_session, _musicProvider));

		// session activity needed for notification click action
		// todo: look into request codes
		final Intent intent = new Intent(this, NowPlayingActivity.class);
		_session.setSessionActivity(PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT));
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

	// belongs to AudioManager.OnAudioFocusChangeListener interface
	// @Override
	// public void onAudioFocusChange(int focusChange) {
	// 	if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
	// 		_session.getController().getTransportControls().pause();
	// 		_audioManager.abandonAudioFocus(this);
	// 	}
	// }
}
