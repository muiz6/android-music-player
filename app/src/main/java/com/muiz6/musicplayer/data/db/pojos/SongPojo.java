package com.muiz6.musicplayer.data.db.pojos;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class SongPojo {

	@NonNull
	@ColumnInfo(name = "path") private String _path;
	@NonNull
	@ColumnInfo(name = "display_name") // file name
	private String _displayName;
	@ColumnInfo(name = "rowid") private int _rowId;
	@ColumnInfo(name = "title") private String _title; // title defined in file's metadata
	@ColumnInfo(name = "artist") private String _artist;
	@ColumnInfo(name = "duration") private int _duration;

	public SongPojo(String path, @NonNull String displayName) {
		_displayName = displayName;
		_path = path;
	}

	public void setPath(String path) {
		_path = path;
	}

	public String getPath() {
		return _path;
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

	public int getDuration() {
		return _duration;
	}

	public void setDuration(int duration) {
		this._duration = duration;
	}

	public int getRowId() {
		return _rowId;
	}

	public void setRowId(int rowId) {
		this._rowId = rowId;
	}
}
