package com.muiz6.musicplayer.ui.main.home.library.albums;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.muiz6.musicplayer.data.db.pojos.AlbumPojo;

public class AlbumItemModel extends AlbumPojo {

	public static final int UNDEFINED = -1;

	private Bitmap _albumArt;
	private int _songCount = UNDEFINED;

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
