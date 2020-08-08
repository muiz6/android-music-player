package com.muiz6.musicplayer.ui.main.home.library.albums;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class AlbumItemModel {

	public static final int UNDEFINED = -1;

	private String _album;
	private String _artist;
	private Bitmap _albumArt;
	private int _songCount = UNDEFINED;

	public void setAlbumTitle(@Nullable String album) {
		_album = album;
	}

	public void setArtist(@Nullable String artist) {
		_artist = artist;
	}

	@Nullable
	public String getAlbumTitle() {
		return _album;
	}

	@Nullable
	public String getArtist() {
		return _artist;
	}

	@Nullable
	public Bitmap getAlbumArt() {
		return _albumArt;
	}

	public void setAlbumArt(@Nullable Bitmap albumArt) {
		this._albumArt = albumArt;
	}

	public int getSongCount() {
		return _songCount;
	}

	public void setSongCount(int songCount) {
		this._songCount = songCount;
	}
}
