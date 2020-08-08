package com.muiz6.musicplayer.ui.main.home.library.genres;

import androidx.annotation.Nullable;

public class GenreItemModel {

	public static final int UNDEFINED = -1;

	private String _genre;
	private String _artist;
	private int _songCount = UNDEFINED;

	@Nullable
	public String getGenre() {
		return _genre;
	}

	public void setGenre(String genre) {
		this._genre = genre;
	}

	@Nullable
	public String getArtist() {
		return _artist;
	}

	public void setArtist(String artist) {
		this._artist = artist;
	}

	public int getSongCount() {
		return _songCount;
	}

	public void setSongCount(int songCount) {
		this._songCount = songCount;
	}
}
