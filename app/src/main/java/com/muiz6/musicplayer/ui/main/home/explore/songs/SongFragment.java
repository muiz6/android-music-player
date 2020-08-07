package com.muiz6.musicplayer.ui.main.home.explore.songs;

import android.os.Bundle;
import android.os.Parcelable;
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

import com.muiz6.musicplayer.BuildConfig;
import com.muiz6.musicplayer.databinding.FragmentListBinding;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class SongFragment extends Fragment
		implements RecyclerView.OnItemTouchListener {

	private static final String _TAG = "SongFragment";
	private static final String _BUNDLE_RECYCLER_LAYOUT = BuildConfig.APPLICATION_ID
			+ ".recycler.layout";
	private final ViewModelProvider.Factory _viewModelFactory;
	private final GestureDetector _gestureDetector;
	private int _activeItemIndex = RecyclerView.NO_POSITION;
	private FragmentListBinding _binding;
	private SongAdapter _songListRecyclerAdapter;
	private SongViewModel _viewModel;

	// public arged ctor to use with fragment factory
	@Inject
	public SongFragment(ViewModelProvider.Factory viewModelFactory, GestureDetector gestureDetector) {
		_viewModelFactory = viewModelFactory;
		_gestureDetector = gestureDetector;
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
				Collections.<SongItemModel>emptyList());
		// _songListRecyclerAdapter.restore;
		_binding = FragmentListBinding.inflate(inflater, container, false);
		final RecyclerView recyclerView = _binding.getRoot();
		recyclerView.setAdapter(_songListRecyclerAdapter);
		recyclerView.addOnItemTouchListener(this);
		return recyclerView;
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);

		if (savedInstanceState != null) {
			final Parcelable savedRecyclerLayoutState = savedInstanceState
					.getParcelable(_BUNDLE_RECYCLER_LAYOUT);
			_binding.getRoot().getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		_viewModel.getSongList().observe(getViewLifecycleOwner(),
				new Observer<List<SongItemModel>>() {

					@Override
					public void onChanged(List<SongItemModel> mediaItems) {
						_songListRecyclerAdapter.setSongList(mediaItems);
					}
				});

		// notify recycler view item when active item is changed
		_viewModel.getPlayingItemIndex().observe(getViewLifecycleOwner(), new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				if (integer != RecyclerView.NO_POSITION) {
					if (_activeItemIndex != RecyclerView.NO_POSITION) {
						_songListRecyclerAdapter.notifyItemChanged(_activeItemIndex);
					}

					// +1 offset due to fragment title
					_activeItemIndex = integer + 1;
					_songListRecyclerAdapter.notifyItemChanged(_activeItemIndex);
				}
			}
		});
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		// save recyclerview state across configuration changes
		// todo: causing crash
		// final RecyclerView.LayoutManager layoutManager = _binding.getRoot().getLayoutManager();
		// if (layoutManager != null) {
		// 	outState.putParcelable(_BUNDLE_RECYCLER_LAYOUT, layoutManager.onSaveInstanceState());
		// }
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
