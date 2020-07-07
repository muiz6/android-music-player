package com.muiz6.musicplayer.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.muiz6.musicplayer.ui.MediaBrowserProvider;

public abstract class MediaBrowserFragment extends Fragment {

	private MediaBrowserProvider _provider;

	public MediaBrowserFragment() {}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		// If used on an activity that doesn't implement MediaBrowserProvider,
		// it will throw an exception as expected
		_provider = (MediaBrowserProvider) context;
	}

	public MediaBrowserProvider getMediaBrowserProvider() {
		return _provider;
	}
}
