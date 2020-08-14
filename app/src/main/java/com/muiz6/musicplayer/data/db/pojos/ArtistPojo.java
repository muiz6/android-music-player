package com.muiz6.musicplayer.data.db.pojos;

import androidx.room.ColumnInfo;

public class ArtistPojo {

	@ColumnInfo(name = "artist") private String _artist;
	@ColumnInfo(name = "artist_id") private int _artistId;

	public String getArtist() {
		return _artist;
	}

	public void setArtist(String artist) {
		this._artist = artist;
	}

	public int getArtistId() {
		return _artistId;
	}

	public void setArtistId(int artistId) {
		this._artistId = artistId;
	}
}
