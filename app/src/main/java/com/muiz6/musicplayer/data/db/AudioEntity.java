package com.muiz6.musicplayer.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "audio_table")
public class AudioEntity {

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private int _id;
	@NonNull
	@ColumnInfo(name = "path")
	private String _path; // contains uri of audio file
	@NonNull
	@ColumnInfo(name = "display_name") // file name
	private String _displayName;
	@ColumnInfo(name = "title") private String _title; // title defined in file's metadata
	@ColumnInfo(name = "album") private String _album;
	@ColumnInfo(name = "artist") private String _artist;
	@ColumnInfo(name = "duration") private int _duration;
	@ColumnInfo(name = "genre") private String _genre;
	// @ColumnInfo(name = "album_art") private String _albumArt;

	public AudioEntity(@NonNull String path, @NonNull String displayName) {
		_path = path;
		_displayName = displayName;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
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

	public void setAlbum(String album) {
		_album = album;
	}

	public String getAlbum() {
		return _album;
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

	public String getGenre() {
		return _genre;
	}

	public void setGenre(String genre) {
		this._genre = genre;
	}
}
