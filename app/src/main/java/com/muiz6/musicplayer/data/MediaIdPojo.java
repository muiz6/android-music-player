package com.muiz6.musicplayer.data;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class MediaIdPojo {

	public static final String CATEGORY_ROOT = "category.root";
	public static final String CATEGORY_LIBRARY = "category.library";
	public static final String CATEGORY_SONG = "category.song";
	public static final String CATEGORY_ALBUM = "category.album";
	public static final String CATEGORY_ARTIST = "category.artist";
	public static final String CATEGORY_GENRE = "category.genre";
	public static final int ID_ROOT = 0;
	public static final int ID_LIBRARY_SONGS = 1;
	public static final int ID_LIBRARY_ALBUMS = 2;
	public static final int ID_LIBRARY_ARTISTS = 3;
	public static final int ID_LIBRARY_GENRES = 4;

	private static final Gson _GSON = new Gson();
	private String _parentCategory;
	private int _parentId;
	private String _category;
	private int _id;

	public MediaIdPojo() {}

	public MediaIdPojo(String category, int id, String parentCategory, int parentId) {
		_category = category;
		_id = id;
		_parentCategory = parentCategory;
		_parentId = parentId;
	}

	public static MediaIdPojo fromString(@NonNull String mediaId) {
		return _GSON.fromJson(mediaId, MediaIdPojo.class);
	}

	public String getParentCategory() {
		return _parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this._parentCategory = parentCategory;
	}

	public int getParentId() {
		return _parentId;
	}

	public void setParentId(int parentId) {
		this._parentId = parentId;
	}

	public String getCategory() {
		return _category;
	}

	public void setCategory(String category) {
		this._category = category;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		this._id = id;
	}

	@NonNull
	@Override
	public String toString() {
		return _GSON.toJson(this);
	}
}
