package com.muiz6.musicplayer.musicservice;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.permission.PermissionManager;

import javax.inject.Inject;
import javax.inject.Named;

public class NotificationDescriptionAdapter
		implements PlayerNotificationManager.MediaDescriptionAdapter {

	private static final String _TAG = "NotificationAdapter";
	private final Context _context;
	private final MediaControllerCompat _mediaController;
	private final PermissionManager _permissionManager;
	private Bitmap _albumArt;
	private Uri _currentUri;

	@Inject
	public NotificationDescriptionAdapter(MediaControllerCompat controller,
			@Named("Application") Context context,
			PermissionManager permissionManager) {
		_mediaController = controller;
		_context = context;
		_permissionManager = permissionManager;
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
		final String album = _mediaController.getMetadata().getBundle()
				.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
		if (album != null) {
			return album;
		}
		return _context.getString(R.string.unknown_album);
	}

	@Nullable
	@Override
	public Bitmap getCurrentLargeIcon(@NonNull Player player,
			@NonNull PlayerNotificationManager.BitmapCallback callback) {
		final Uri uri = _mediaController.getMetadata().getDescription().getMediaUri();
		if (_permissionManager.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
				&& uri != null) {
			if (!uri.equals(_currentUri)) {
				_currentUri = uri;

				// loading bitmap can block thread so loading in bg thread
				new Thread(new _AsyncLoadBitmap(callback)).start();
			}
			return _albumArt;
		}
		return null;
	}

	private class _AsyncLoadBitmap implements Runnable {

		private final PlayerNotificationManager.BitmapCallback _callback;

		public _AsyncLoadBitmap(@NonNull PlayerNotificationManager.BitmapCallback callback) {
			_callback = callback;
		}

		@Override
		public void run() {
			final MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
			try {
				metadataRetriever.setDataSource(_context, _currentUri);
				final byte[] byteArr = metadataRetriever.getEmbeddedPicture();

				// todo: set alternative bitmap instead of null
				Bitmap albumArt = null;
				if (byteArr != null) {
					final int size = _context.getResources()
							.getDimensionPixelSize(R.dimen.size_notification_img);
					albumArt = Bitmap.createScaledBitmap(
							BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length),
							size,
							size,
							false);
				}
				_albumArt = albumArt;
				_callback.onBitmap(albumArt);
			}
			catch (Exception e) {
				Log.e(_TAG, "Exception caught!", e);
			}
			metadataRetriever.release();
		}
	}
}
