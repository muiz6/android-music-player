package com.muiz6.musicplayer.ui.main.songs;

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
import com.muiz6.musicplayer.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SongFragment extends Fragment {

	private static final String _TAG = "SongFragment";
	private RecyclerView _recyclerView;
	private _SongAdapter _songListRecyclerAdapter;
	private ArrayList<MediaBrowserCompat.MediaItem> _songList;

	// required public ctor
	public SongFragment() {}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		_songList = new ArrayList<>();

		// initializing adapter with empty songlist
		_songListRecyclerAdapter =
				new _SongAdapter(_songList, getActivity());

		_recyclerView = (RecyclerView) LayoutInflater.from(context)
				.inflate(R.layout.fragment_list, null);

		_recyclerView.setAdapter(_songListRecyclerAdapter);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return _recyclerView;
	}

	@Override
	public void onStart() {
		super.onStart();

		MainActivity activity = (MainActivity) getActivity();
		activity.getMediaBrowser().subscribe(MusicProvider.MEDIA_ID_ALL_SONGS,
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

		((MainActivity)getActivity()).getMediaBrowser().unsubscribe(MusicProvider.MEDIA_ID_ALL_SONGS);
	}
}
