package com.muiz6.musicplayer.ui.main.home.library.artists;

import com.muiz6.musicplayer.data.db.pojos.ArtistPojo;

public class ArtistItemModel extends ArtistPojo {

	public static final int UNDEFINED = -1;

	private int _albumCount = UNDEFINED;
	private int _songCount = UNDEFINED;

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
