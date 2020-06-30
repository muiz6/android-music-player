package com.muiz6.musicplayer.notification;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

	private final MediaControllerCompat _controller;

	public DescriptionAdapter(MediaControllerCompat controller) {
		_controller = controller;
	}

	@NonNull
	@Override
	public CharSequence getCurrentContentTitle(@NonNull Player player) {
		final String title = _controller.getMetadata()
				.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
		if (title != null) {
			return title;
		}
		return "Unknown Title";
	}

	@Nullable
	@Override
	public PendingIntent createCurrentContentIntent(@NonNull Player player) {
		return _controller.getSessionActivity();
	}

	@Nullable
	@Override
	public CharSequence getCurrentContentText(@NonNull Player player) {
		final String content = _controller.getMetadata()
				.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
		if (content != null) {
			return content;
		}
		return "Unknown Artist";
	}

	@Nullable
	@Override
	public CharSequence getCurrentSubText(Player player) {
		final String subText = _controller.getMetadata()
				.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION);
		if (subText != null) {
			return subText;
		}
		return "Unknown Album";
	}

	@Nullable
	@Override
	public Bitmap getCurrentLargeIcon(@NonNull Player player,
			@NonNull PlayerNotificationManager.BitmapCallback callback) {
		// todo: add alternative bitmap here
		return _controller.getMetadata().getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
	}
}
