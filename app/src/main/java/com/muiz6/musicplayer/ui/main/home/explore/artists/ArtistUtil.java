package com.muiz6.musicplayer.ui.main.home.explore.artists;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import java.util.ArrayList;
import java.util.List;

public abstract class ArtistUtil {

	public static List<ArtistItemModel> getArtistList(
			List<MediaBrowserCompat.MediaItem> artistItemList) {
		final List<ArtistItemModel> newArtists = new ArrayList<>();
		for (final MediaBrowserCompat.MediaItem artist: artistItemList) {
			final ArtistItemModel model = new ArtistItemModel();
			final MediaDescriptionCompat description = artist.getDescription();
			model.setName(String.valueOf(description.getTitle()));
			newArtists.add(model);
		}
		return newArtists;
	}
}
