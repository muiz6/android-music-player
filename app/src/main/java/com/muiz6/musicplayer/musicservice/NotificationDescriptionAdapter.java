package com.muiz6.musicplayer.musicservice;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.R;

import javax.inject.Inject;
import javax.inject.Named;

public class NotificationDescriptionAdapter
		implements PlayerNotificationManager.MediaDescriptionAdapter {

	private final Context _context;
	private final MediaControllerCompat _mediaController;

	@Inject
	public NotificationDescriptionAdapter(MediaControllerCompat controller,
			@Named("Application") Context context) {
		_mediaController = controller;
		_context = context;
	}

	@NonNull
	@Override
	public CharSequence getCurrentContentTitle(@NonNull Player player) {
		final String title = _mediaController.getMetadata()
				.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
		if (title != null) {
			return title;
		}
		return _context.getString(R.string.unknown_title);
	}

	@Nullable
	@Override
	public PendingIntent createCurrentContentIntent(@NonNull Player player) {
		return _mediaController.getSessionActivity();
	}

	@Nullable
	@Override
	public CharSequence getCurrentContentText(@NonNull Player player) {
		final String subtitle = _mediaController.getMetadata()
				.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
		if (subtitle != null) {
			return subtitle;
		}
		return _context.getString(R.string.unknown_artist);
	}

	@Nullable
	@Override
	public CharSequence getCurrentSubText(Player player) {
		final String descritption = _mediaController.getMetadata()
				.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION);
		if (descritption != null) {
			return descritption;
		}
		return _context.getString(R.string.unknown_album);
	}

	@Nullable
	@Override
	public Bitmap getCurrentLargeIcon(@NonNull Player player,
			@NonNull PlayerNotificationManager.BitmapCallback callback) {
		final MediaMetadataCompat metadata = _mediaController.getMetadata();
		if (metadata != null) {
			return metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
		}
		return null;
	}
}
