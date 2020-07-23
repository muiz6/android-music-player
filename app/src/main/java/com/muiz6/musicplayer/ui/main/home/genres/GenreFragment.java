package com.muiz6.musicplayer.ui.main.home.genres;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
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

public class GenreFragment extends Fragment {

	private final ViewModelProvider.Factory _viewModelFactory;
	private GenreViewModel _viewModel;
	private FragmentListBinding _binding;

	@Inject
	public GenreFragment(ViewModelProvider.Factory viewModelFactory) {
		_viewModelFactory = viewModelFactory;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(GenreViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentListBinding.inflate(inflater);
		final RecyclerView recyclerView = _binding.getRoot();
		recyclerView.setAdapter(new GenreAdapter(
				Collections.<MediaBrowserCompat.MediaItem>emptyList()));
		return recyclerView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		_viewModel.getGenreList().observe(getViewLifecycleOwner(),
				new Observer<List<MediaBrowserCompat.MediaItem>>() {

					@Override
					public void onChanged(List<MediaBrowserCompat.MediaItem> mediaItems) {
						_binding.getRoot().setAdapter(new GenreAdapter(mediaItems));
					}
				});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		_binding = null;
	}
}
