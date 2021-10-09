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

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.permission.PermissionManager;

import java.util.ArrayList;
import java.util.List;

public abstract class SongUtil {

	private static final String _TAG = "SongUtil";

	public static void getSongList(@NonNull List<MediaBrowserCompat.MediaItem> list,
			@NonNull Context context,
			@NonNull PermissionManager permissionManager,
			@NonNull Callback callback) {
		final List<SongItemModel> newSongList = new ArrayList<>();
		for (final MediaBrowserCompat.MediaItem mediaItem : list) {
			final SongItemModel model = new SongItemModel();
			final MediaDescriptionCompat description = mediaItem.getDescription();
			model.setTitle(String.valueOf(description.getTitle()));
			if (description.getSubtitle() != null) {
				model.setArtist(String.valueOf(description.getSubtitle()));
			}
			else {
				final String altArtist = context.getResources().getString(R.string.unknown_artist);
				model.setArtist(altArtist);
			}
			final int duration = description.getExtras()
					.getInt(MediaMetadataCompat.METADATA_KEY_DURATION);
			model.setDuration(duration);
			newSongList.add(model);
		}
		callback.onResult(newSongList);

		// retrieve album art asynchronously
		if (permissionManager.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
			final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			for (int i = 0; i < newSongList.size(); i++) {

				// retrieve album art and add in model
				try {
					final MediaDescriptionCompat description = list.get(i).getDescription();
					retriever.setDataSource(context, description.getMediaUri());
					byte[] byteArr = retriever.getEmbeddedPicture();
					if (byteArr != null) {
						final SongItemModel model = newSongList.get(i);
						final Bitmap bmp = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
						model.setAlbumArt(Bitmap
								.createScaledBitmap(bmp, 70, 70, false));
					}
				}
				catch (Exception e) {
					Log.e(_TAG, "Exception caught!", e);
				}
			}
			callback.onResult(newSongList);
			retriever.release();
		}
	}

	/**
	 * A callback that is called when data is first ready to initially displayed,
	 * and called again when images have been loaded
	 */
	public interface Callback {
		void onResult(List<SongItemModel> resultList);
	}
}
