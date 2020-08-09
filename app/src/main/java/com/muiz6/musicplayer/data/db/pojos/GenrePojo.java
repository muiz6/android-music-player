package com.muiz6.musicplayer.data.db.pojos;

import androidx.room.ColumnInfo;

public class GenrePojo {

	@ColumnInfo(name = "genre") private String _genre;

	public String getGenre() {
		return _genre;
	}

	public void setGenre(String genre) {
		this._genre = genre;
	}
}
