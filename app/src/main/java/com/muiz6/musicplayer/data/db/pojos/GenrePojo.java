package com.muiz6.musicplayer.data.db.pojos;

import androidx.room.ColumnInfo;

public class GenrePojo {

	@ColumnInfo(name = "genre") private String _genre;
	@ColumnInfo(name = "genre_id") private int _genreId;

	public String getGenre() {
		return _genre;
	}

	public void setGenre(String genre) {
		this._genre = genre;
	}

	public int getGenreId() {
		return _genreId;
	}

	public void setGenreId(int genreId) {
		this._genreId = genreId;
	}
}
