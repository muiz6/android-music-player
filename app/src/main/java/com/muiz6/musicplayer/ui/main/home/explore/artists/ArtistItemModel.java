package com.muiz6.musicplayer.ui.main.home.explore.artists;

import androidx.annotation.Nullable;

public class ArtistItemModel {

	public static final int UNDEFINED = -1;

	private String _name;
	private int _albumCount = UNDEFINED;
	private int _songCount = UNDEFINED;

	@Nullable
	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	/**
	 * @return count of albums by the artist or ArtistItemModel#UNDEFINED if not known
	 */
	public int getAlbumCount() {
		return _albumCount;
	}

	public void setAlbumCount(int albumCount) {
		this._albumCount = albumCount;
	}

	/**
	 * @return count of songs by the artist or ArtistItemModel#UNDEFINED if not known
	 */
	public int getSongCount() {
		return _songCount;
	}

	public void setSongCount(int songCount) {
		this._songCount = songCount;
	}
}
