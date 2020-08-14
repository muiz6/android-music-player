package com.muiz6.musicplayer.data.db.pojos;

import androidx.room.ColumnInfo;

public class AlbumPojo {

	@ColumnInfo(name = "album") private String _album;
	@ColumnInfo(name = "artist") private String _artist;
	@ColumnInfo(name = "album_id") private int _albumId;

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

	public int getAlbumId() {
		return _albumId;
	}

	public void setAlbumId(int albumId) {
		this._albumId = albumId;
	}
}
