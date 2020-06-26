package com.muiz6.musicplayer.musicservice.notification;

import android.app.Notification;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.muiz6.musicplayer.App;
import com.muiz6.musicplayer.R;

class _ControllerCallback extends MediaControllerCompat.Callback {

	private final MediaBrowserServiceCompat _service;
	private final NotificationCompat.Builder _builder;

	_ControllerCallback(MediaBrowserServiceCompat service, NotificationCompat.Builder builder) {
		_service = service;
		_builder = builder;
	}

	@Override
	public void onMetadataChanged(MediaMetadataCompat metadata) {
		super.onMetadataChanged(metadata);

		if (metadata != null) {
			final String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
			final String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
			final String album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
			if (title != null) {
				_builder.setContentTitle(title);
			}
			else {
				_builder.setContentTitle("Title");
			}
			if (artist != null) {
				_builder.setContentText(artist);
			}
			else {
				_builder.setContentText("Unknown Artist");
			}
			if (album != null) {
				_builder.setSubText(album);
			}
			else {
				_builder.setSubText("Unknown Album");
			}
			NotificationManagerCompat.from(_service).notify(App.NOTIFICATION_ID_PLAYER,
					_builder.build());
		}
	}

	@Override
	public void onPlaybackStateChanged(PlaybackStateCompat state) {
		super.onPlaybackStateChanged(state);

		if (state != null) {
			int pbState = state.getState();
			if (pbState == PlaybackStateCompat.STATE_PLAYING) {
				Notification notif = _builder.build();
				notif.actions[1] = new Notification.Action(R.drawable.ic_pause_black_24dp,
						"play_pause",
						MediaButtonReceiver.buildMediaButtonPendingIntent(_service,
								PlaybackStateCompat.ACTION_PLAY_PAUSE));
				_service.startForeground(App.NOTIFICATION_ID_PLAYER, notif);
			}
			else {
				NotificationManagerCompat.from(_service).notify(App.NOTIFICATION_ID_PLAYER,
						_builder.build());
				_service.stopForeground(false);
			}
		}
	}
}
