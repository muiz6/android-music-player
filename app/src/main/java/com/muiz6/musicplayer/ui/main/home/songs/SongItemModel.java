package com.muiz6.musicplayer.ui.main.home.songs;

import androidx.annotation.Nullable;

public class SongItemModel {

	private String _title;
	private String _artist;
	private int _duration;

	public void setTitle(String title) {
		_title = title;
	}

	@Nullable
	public String getTitle() {
		return _title;
	}

	public void setArtist(String artist) {
		_artist = artist;
	}

	@Nullable
	public String getArtist() {
		return _artist;
	}

	public void setDuration(int duration) {
		_duration = duration;
	}

	public int getDuration() {
		return _duration;
	}
}
