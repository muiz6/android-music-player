package com.muiz6.musicplayer.ui.main.songs;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.musicprovider.MusicProvider;
import com.muiz6.musicplayer.ui.main.MediaBrowserFragment;

import java.util.ArrayList;
import java.util.List;

public class SongFragment extends MediaBrowserFragment {

	private static final String _TAG = "SongFragment";
	private RecyclerView _recyclerView;
	private _SongAdapter _songListRecyclerAdapter;
	private ArrayList<MediaBrowserCompat.MediaItem> _songList;

	// required public ctor
	public SongFragment() {}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_songList = new ArrayList<>();

		// initializing adapter with empty songlist
		_songListRecyclerAdapter = new _SongAdapter(_songList, getActivity());
		_recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_list, null);
		_recyclerView.setAdapter(_songListRecyclerAdapter);
		return _recyclerView;
	}

	@Override
	public void onStart() {
		super.onStart();

		final MediaBrowserCompat browser = getMediaBrowserProvider().getMediaBrowser();
		browser.subscribe(MusicProvider.MEDIA_ID_ALL_SONGS,
				new MediaBrowserCompat.SubscriptionCallback() {
					@Override
					public void onChildrenLoaded(@NonNull String parentId,
							@NonNull List<MediaBrowserCompat.MediaItem> children) {
						super.onChildrenLoaded(parentId, children);

						_songList = (ArrayList<MediaBrowserCompat.MediaItem>) children;
						_songListRecyclerAdapter
								.setSongList(_songList);
					}
				});
	}

	@Override
	public void onStop() {
		super.onStop();

		final MediaBrowserCompat browser = getMediaBrowserProvider().getMediaBrowser();
		browser.unsubscribe(MusicProvider.MEDIA_ID_ALL_SONGS);
	}

	public static SongFragment newInstance() {
		return new SongFragment();
	}
}
