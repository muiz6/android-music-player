package com.muiz6.musicplayer.ui.main;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.BuildConfig;

/**
 * view model class to share search query data among fragments
 */
public class SharedQueryViewModel extends ViewModel {

	public static final String KEY_TYPE = BuildConfig.APPLICATION_ID + "key.uri.type";
	public static final String KEY_VALUE = BuildConfig.APPLICATION_ID + "Key.uri.value";
	public static final String TYPE_SEARCH = BuildConfig.APPLICATION_ID + "type.uri.search";
	public static final String TYPE_ARTIST = BuildConfig.APPLICATION_ID + "type.uri.artist";
	public static final String TYPE_GENRE = BuildConfig.APPLICATION_ID + "type.uri.genre";

	private final MutableLiveData<Uri> _uri = new MutableLiveData<>(Uri.EMPTY);

	public LiveData<Uri> getSearchUri() {
		return _uri;
	}

	public void setSearchUri(Uri uri) {
		_uri.postValue(uri);
	}
}
