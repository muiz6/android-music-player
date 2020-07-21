package com.muiz6.musicplayer.ui.main.home.songs;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.databinding.FragmentListBinding;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class SongFragment extends Fragment
		implements RecyclerView.OnItemTouchListener {

	private static final String _TAG = "SongFragment";
	private final ViewModelProvider.Factory _viewModelFactory;
	private Integer _activeItemIndex = SongViewModel.NOTHING_PLAYING;
	private FragmentListBinding _binding;
	private SongAdapter _songListRecyclerAdapter;
	private GestureDetector _gestureDetector;
	private SongViewModel _viewModel;

	// public arged ctor to use with fragment factory
	@Inject
	public SongFragment(ViewModelProvider.Factory viewModelFactory) {
		_viewModelFactory = viewModelFactory;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(SongViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {

		// initializing adapter with empty song list
		_songListRecyclerAdapter = new SongAdapter(
				Collections.<MediaBrowserCompat.MediaItem>emptyList());
		_gestureDetector = new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return true;
					}
				});
		_binding = FragmentListBinding.inflate(inflater, container, false);
		final RecyclerView recyclerView = _binding.getRoot();
		recyclerView.setAdapter(_songListRecyclerAdapter);
		recyclerView.addOnItemTouchListener(this);
		return recyclerView;
	}

	@Override
	public void onStart() {
		super.onStart();

		_viewModel.getSongList().observe(getViewLifecycleOwner(), new Observer<List<MediaBrowserCompat.MediaItem>>() {

			@Override
			public void onChanged(List<MediaBrowserCompat.MediaItem> mediaItems) {
				_songListRecyclerAdapter.setSongList(mediaItems);
			}
		});
		// _viewModel.getPlayingItemIndex().observe(getViewLifecycleOwner(), new Observer<Integer>() {
		// 	@Override
		// 	public void onChanged(Integer integer) {
		// 		if (integer != SongViewModel.NOTHING_PLAYING) {
		// 			// if (_activeItemIndex != SongViewModel.NOTHING_PLAYING) {
		// 			// 	final View oldView = _recyclerView.getChildAt(_activeItemIndex);
		// 			// 	oldView.setBackground(null);
		// 			// }
		//
		// 			// +1 offset due to fragment title
		// 			_activeItemIndex = integer + 1;
		// 			final RecyclerView.ViewHolder newViewHolder = _recyclerView
		// 					.findViewHolderForAdapterPosition(_activeItemIndex);
		// 			if (newViewHolder != null) {
		// 				newViewHolder.itemView.setBackground(getContext()
		// 						.getDrawable(R.drawable.active_queue_item_bg));
		// 			}
		// 		}
		// 	}
		// });
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		_binding = null;
	}

	@Override
	public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
		final View view = rv.findChildViewUnder(e.getX(), e.getY());
		if (view != null && _gestureDetector.onTouchEvent(e)) {
			int index = rv.getChildAdapterPosition(view);
			if (index > 0) {
				_viewModel.songItemClicked(index - 1);
			}
		}
		return false;
	}

	@Override
	public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}
