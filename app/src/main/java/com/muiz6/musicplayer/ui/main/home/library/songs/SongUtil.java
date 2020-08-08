package com.muiz6.musicplayer.ui.main.home.library.songs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

public abstract class SongUtil {

	public static List<SongItemModel> getSongList(List<MediaBrowserCompat.MediaItem> list,
			Context context) {
		final List<SongItemModel> newSongList = new ArrayList<>();
		final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		for (final MediaBrowserCompat.MediaItem mediaItem : list) {
			final SongItemModel model = new SongItemModel();
			final MediaDescriptionCompat description = mediaItem.getDescription();
			model.setTitle(String.valueOf(description.getTitle()));
			model.setArtist(String.valueOf(description.getSubtitle()));
			final int duration = description.getExtras()
					.getInt(MediaMetadataCompat.METADATA_KEY_DURATION);
			model.setDuration(duration);

			// retrieve album art and add in model
			retriever.setDataSource(context, description.getMediaUri());
			byte[] byteArr = retriever.getEmbeddedPicture();
			if (byteArr != null) {
				final Bitmap bmp = BitmapFactory
						.decodeByteArray(byteArr, 0, byteArr.length);
				model.setAlbumArt(Bitmap
						.createScaledBitmap(bmp, 70, 70, false));
			}
			newSongList.add(model);
		}
		return newSongList;
	}
}
