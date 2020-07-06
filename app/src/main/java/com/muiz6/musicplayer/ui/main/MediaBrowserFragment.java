package com.muiz6.musicplayer.ui.main;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class MediaBrowserFragment extends Fragment {

	private MediaFragmentListener _mediaFragmentListener;

	public MediaBrowserFragment() {}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		// If used on an activity that doesn't implement MediaBrowserProvider,
		// it will throw an exception as expected
		_mediaFragmentListener = (MediaFragmentListener) context;
	}

	public MediaFragmentListener getMediaFragmentListener() {
		return _mediaFragmentListener;
	}

	public interface MediaFragmentListener {

		@Nullable
		MediaBrowserCompat getMediaBrowser();
	}
}
