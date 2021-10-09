package com.muiz6.musicplayer.ui.main.home.library.songs;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class SongItemModel {

	private String _title;
	private String _artist;
	private int _duration;
	private boolean _isActive = false;
	private Bitmap _albumArt;

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

	public boolean isActive() {
		return _isActive;
	}

	public void setActive(boolean state) {
		_isActive = state;
	}

	public void setAlbumArt(Bitmap art) {
		_albumArt = art;
	}

	@Nullable
	public Bitmap getAlbumArt() {
		return _albumArt;
	}
}
