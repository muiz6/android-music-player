package com.muiz6.musicplayer.data.db.pojos;

import androidx.room.ColumnInfo;

public class ArtistPojo {

	@ColumnInfo(name = "artist") private String _artist;

	public String getArtist() {
		return _artist;
	}

	public void setArtist(String artist) {
		this._artist = artist;
	}
}
