package com.muiz6.musicplayer.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

@Fts4
@Entity(tableName = "audio_table")
public class AudioEntity {

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "rowid")
	private int _rowid;
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
	@ColumnInfo(name = "album_id") private int _albumId;
	@ColumnInfo(name = "artist_id") private int _artistId;
	@ColumnInfo(name = "genre_id") private int _genreId;

	public AudioEntity(@NonNull String path, @NonNull String displayName) {
		_path = path;
		_displayName = displayName;
	}

	// casing has to be like this for primary key to be full text compatible according to docs
	// room will not let compile if private field name is not similar to column/getter/setter name
	public int getRowid() {
		return _rowid;
	}

	public void setRowid(int id) {
		_rowid = id;
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

	public void setAlbumId(int albumId) {
		_albumId = albumId;
	}

	public int getAlbumId() {
		return _albumId;
	}

	public void setArtistId(int artistId) {
		_artistId = artistId;
	}

	public int getArtistId() {
		return _artistId;
	}

	public void setGenreId(int genreId) {
		_genreId = genreId;
	}

	public int getGenreId() {
		return _genreId;
	}
}
