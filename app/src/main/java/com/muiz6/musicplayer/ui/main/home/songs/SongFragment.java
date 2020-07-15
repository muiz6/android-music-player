package com.muiz6.musicplayer.ui.main.home.songs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.musicprovider.MusicProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SongFragment extends Fragment {

	private static final String _TAG = "SongFragment";
	private final MediaBrowserCompat _mediaBrowser;
	private RecyclerView _recyclerView;
	private SongAdapter _songListRecyclerAdapter;
	private ArrayList<MediaBrowserCompat.MediaItem> _songList;

	// public arged ctor to use with fragment factory
	@Inject
	public SongFragment(MediaBrowserCompat mediaBrowser) {
		_mediaBrowser = mediaBrowser;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_songList = new ArrayList<>();

		// initializing adapter with empty songlist
		_songListRecyclerAdapter = new SongAdapter(_songList, _mediaBrowser);
		_recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_list, container, false);
		_recyclerView.setAdapter(_songListRecyclerAdapter);
		return _recyclerView;
	}

	@Override
	public void onStart() {
		super.onStart();

		_mediaBrowser.subscribe(MusicProvider.MEDIA_ID_ALL_SONGS,
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

		_mediaBrowser.unsubscribe(MusicProvider.MEDIA_ID_ALL_SONGS);
	}
}
