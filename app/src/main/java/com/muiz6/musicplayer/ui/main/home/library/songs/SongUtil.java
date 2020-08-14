package com.muiz6.musicplayer.ui.main.home.library.songs;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.muiz6.musicplayer.permission.PermissionManager;

import java.util.ArrayList;
import java.util.List;

public abstract class SongUtil {

	private static final String _TAG = "SongUtil";

	public static List<SongItemModel> getSongList(@NonNull List<MediaBrowserCompat.MediaItem> list,
			@NonNull Context context, PermissionManager permissionManager) {
		final List<SongItemModel> newSongList = new ArrayList<>();
		for (final MediaBrowserCompat.MediaItem mediaItem : list) {
			final SongItemModel model = new SongItemModel();
			final MediaDescriptionCompat description = mediaItem.getDescription();
			model.setTitle(String.valueOf(description.getTitle()));
			model.setArtist(String.valueOf(description.getSubtitle()));
			final int duration = description.getExtras()
					.getInt(MediaMetadataCompat.METADATA_KEY_DURATION);
			model.setDuration(duration);
			newSongList.add(model);
		}
		if (permissionManager.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
			new Thread(new _AsyncFetch(list, newSongList, context)).start();
		}
		return newSongList;
	}

	// public interface Callback {
	// 	void onItemChange(int index);
	// }

	private static class _AsyncFetch implements Runnable {

		private final List<MediaBrowserCompat.MediaItem> _mediaItemList;
		private final List<SongItemModel> _songItemList;
		private final Context _context;

		public _AsyncFetch(List<MediaBrowserCompat.MediaItem> mediaItemList,
				List<SongItemModel> songItems,
				Context context) {
			_mediaItemList = mediaItemList;
			_songItemList = songItems;
			_context = context;
		}

		@Override
		public void run() {
			final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			for (int i = 0; i < _songItemList.size(); i++) {

				// retrieve album art and add in model
				try {
					final MediaDescriptionCompat description = _mediaItemList.get(i).getDescription();
					retriever.setDataSource(_context, description.getMediaUri());
					byte[] byteArr = retriever.getEmbeddedPicture();
					if (byteArr != null) {
						final SongItemModel model = _songItemList.get(i);
						final Bitmap bmp = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
						model.setAlbumArt(Bitmap
								.createScaledBitmap(bmp, 70, 70, false));
					}
				}
				catch (Exception e) {
					Log.e(_TAG, "Exception caught!", e);
				}
			}
		}
	}
}
