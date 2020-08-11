package com.muiz6.musicplayer.data;

public class MediaIdPojo {

	public static final String CATEGORY_ROOT = "category.root";
	public static final String CATEGORY_LIBRARY = "category.library";
	public static final String CATEGORY_SONG = "category.song";
	public static final String CATEGORY_ALBUM = "category.album";
	public static final String CATEGORY_ARTIST = "category.artist";
	public static final String CATEGORY_GENRE = "category.genre";
	public static final String VALUE_ROOT = "value.root";
	public static final String VALUE_LIBRARY_SONGS = "value.library.song";
	public static final String VALUE_LIBRARY_ALBUMS = "value.library.album";
	public static final String VALUE_LIBRARY_ARTISTS = "value.library.artist";
	public static final String VALUE_LIBRARY_GENRES = "value.library.genre";

	private String _parentCategory;
	private String _parentValue;
	private String _category;
	private String _value;
	private int _index;

	public MediaIdPojo() {}

	public MediaIdPojo(String category, String value, int index, String parentCategory, String parentValue) {
		_category = category;
		_value = value;
		_index = index;
		_parentCategory = parentCategory;
		_parentValue = parentValue;
	}

	public String getParentCategory() {
		return _parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this._parentCategory = parentCategory;
	}

	public String getParentValue() {
		return _parentValue;
	}

	public void setParentValue(String parentValue) {
		this._parentValue = parentValue;
	}

	public String getCategory() {
		return _category;
	}

	public void setCategory(String category) {
		this._category = category;
	}

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		this._value = value;
	}

	public int getIndex() {
		return _index;
	}

	public void setIndex(int index) {
		_index = index;
	}
}
