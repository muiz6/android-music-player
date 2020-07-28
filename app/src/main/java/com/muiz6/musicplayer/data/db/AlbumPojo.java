package com.muiz6.musicplayer.data.db;

import androidx.room.ColumnInfo;

public class AlbumPojo {

	@ColumnInfo(name = "album") private String _album;
	@ColumnInfo(name = "artist") private String _artist;

	public void setAlbum(String album) {
		_album = album;
	}

	public String getAlbum() {
		return _album;
	}

	public String getArtist() {
		return _artist;
	}

	public void setArtist(String artist) {
		this._artist = artist;
	}
}
