package com.muiz6.musicplayer.ui.main.home.explore.artists;

import android.os.Bundle;
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

public class ArtistFragment extends Fragment {

	private final ViewModelProvider.Factory _viewModelFactory;
	private ArtistViewModel _viewModel;
	private FragmentListBinding _binding;

	@Inject
	public ArtistFragment(ViewModelProvider.Factory viewModelFactory) {
		_viewModelFactory = viewModelFactory;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(ArtistViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentListBinding.inflate(inflater, container, false);
		final RecyclerView recyclerView = _binding.getRoot();
		recyclerView.setAdapter(new ArtistAdapter(
				Collections.<ArtistItemModel>emptyList()));
		return recyclerView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		_viewModel.getArtistList().observe(getViewLifecycleOwner(),
				new Observer<List<ArtistItemModel>>() {

					@Override
					public void onChanged(List<ArtistItemModel> mediaItems) {
						_binding.getRoot().setAdapter(new ArtistAdapter(mediaItems));
					}
				});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		_binding = null;
	}
}
