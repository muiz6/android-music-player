package com.muiz6.musicplayer.notification;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.media.MusicServiceConnection;

import javax.inject.Inject;

public class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

	// private final MediaControllerCompat _controller;
	private final Observer<MediaMetadataCompat> _metadataObserver =
			new Observer<MediaMetadataCompat>() {

				@Override
				public void onChanged(MediaMetadataCompat mediaMetadataCompat) {
					_metadata = mediaMetadataCompat;
				}
			};
	private final MusicServiceConnection _connection;
	private MediaMetadataCompat _metadata;

	@Inject
	public DescriptionAdapter(MusicServiceConnection connection) {
		// _controller = controller;
		_connection = connection;

		// todo: remove observer when not needed
		_connection.getMetadata().observeForever(_metadataObserver);
	}

	@NonNull
	@Override
	public CharSequence getCurrentContentTitle(@NonNull Player player) {
		// final String title = _controller.getMetadata()
		// 		.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
		if (_metadata != null) {
			final String title = _metadata
					.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
			if (title != null) {
				return title;
			}
		}
		return "Unknown Title";
	}

	@Nullable
	@Override
	public PendingIntent createCurrentContentIntent(@NonNull Player player) {
		// return _controller.getSessionActivity();
		return null;
	}

	@Nullable
	@Override
	public CharSequence getCurrentContentText(@NonNull Player player) {
		// final String content = _controller.getMetadata()
		// 		.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
		if (_metadata != null) {
			final String subtitle = _metadata
					.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
			if (subtitle != null) {
				return subtitle;
			}
		}
		return "Unknown Artist";
	}

	@Nullable
	@Override
	public CharSequence getCurrentSubText(Player player) {
		// final String subText = _controller.getMetadata()
		// 		.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION);
		if (_metadata != null) {
			final String descritption = _metadata
					.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION);
			if (descritption != null) {
				return descritption;
			}
		}
		return "Unknown Album";
	}

	@Nullable
	@Override
	public Bitmap getCurrentLargeIcon(@NonNull Player player,
			@NonNull PlayerNotificationManager.BitmapCallback callback) {

		// todo: add alternative bitmap here
		if (_metadata != null) {
			_metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
		}
		return null;
	}
}
