package com.muiz6.musicplayer.ui.main.home.albums;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.databinding.FragmentGridBinding;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class AlbumFragment extends Fragment {

	private final ViewModelProvider.Factory _viewModelFactory;
	private AlbumViewModel _viewModel;
	private FragmentGridBinding _binding;

	@Inject
	public AlbumFragment(ViewModelProvider.Factory viewModelFactory) {
		_viewModelFactory = viewModelFactory;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(AlbumViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentGridBinding.inflate(inflater, container, false);
		final RecyclerView recyclerView = _binding.getRoot();
		recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 3));
		recyclerView.setAdapter(new AlbumAdapter(
				Collections.<MediaBrowserCompat.MediaItem>emptyList()));
		return recyclerView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		_viewModel.getAlbumList().observe(getViewLifecycleOwner(),
				new Observer<List<MediaBrowserCompat.MediaItem>>() {

					@Override
					public void onChanged(List<MediaBrowserCompat.MediaItem> albumList) {
						_binding.getRoot().setAdapter(new AlbumAdapter(albumList));
					}
				});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		_binding = null;
	}
}
