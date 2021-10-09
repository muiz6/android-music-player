package com.muiz6.musicplayer.ui.main.home.library.genres;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

public abstract class GenreUtil {

	public static List<GenreItemModel> getGenreList(
			List<MediaBrowserCompat.MediaItem> genreMediaList) {
		final List<GenreItemModel> newGenres = new ArrayList<>();
		for (MediaBrowserCompat.MediaItem mediaItem : genreMediaList) {
			final GenreItemModel genreItem = new GenreItemModel();
			final Bundle extras = mediaItem.getDescription().getExtras();
			genreItem.setGenre(extras.getString(MediaMetadataCompat.METADATA_KEY_GENRE));
			genreItem.setArtist(extras.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
			newGenres.add(genreItem);
		}
		return newGenres;
	}
}
