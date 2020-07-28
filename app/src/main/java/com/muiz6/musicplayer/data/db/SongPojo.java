package com.muiz6.musicplayer.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class SongPojo {

	@NonNull
	@ColumnInfo(name = "display_name") // file name
	private String _displayName;
	@ColumnInfo(name = "title") private String _title; // title defined in file's metadata
	@ColumnInfo(name = "artist") private String _artist;

	public SongPojo(@NonNull String displayName) {
		_displayName = displayName;
	}

	public void setDisplayName(String displayName) {
		_displayName = displayName;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		this._title = title;
	}

	public String getArtist() {
		return _artist;
	}

	public void setArtist(String artist) {
		this._artist = artist;
	}
}
