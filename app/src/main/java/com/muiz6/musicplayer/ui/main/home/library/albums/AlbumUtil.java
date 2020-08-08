package com.muiz6.musicplayer.ui.main.home.library.albums;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

public abstract class AlbumUtil {

	public static List<AlbumItemModel> getAlbumList(List<MediaBrowserCompat.MediaItem> list,
			Context context) {
		final List<AlbumItemModel> albumItemList = new ArrayList<>();
		for (final MediaBrowserCompat.MediaItem item : list) {
			final AlbumItemModel model = new AlbumItemModel();
			final Bundle extras = item.getDescription().getExtras();
			model.setAlbumTitle(extras.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
			model.setArtist(extras.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

			// todo: retrieve album art and song count
			albumItemList.add(model);
		}
		return albumItemList;
	}
}
