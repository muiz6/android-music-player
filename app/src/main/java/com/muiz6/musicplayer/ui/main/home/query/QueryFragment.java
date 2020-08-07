package com.muiz6.musicplayer.ui.main.home.query;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.muiz6.musicplayer.databinding.FragmentQueryBinding;
import com.muiz6.musicplayer.ui.main.SharedQueryViewModel;
import com.muiz6.musicplayer.ui.main.home.explore.albums.AlbumItemModel;
import com.muiz6.musicplayer.ui.main.home.explore.artists.ArtistAdapter;
import com.muiz6.musicplayer.ui.main.home.explore.artists.ArtistItemModel;
import com.muiz6.musicplayer.ui.main.home.explore.songs.SongAdapter;
import com.muiz6.musicplayer.ui.main.home.explore.songs.SongItemModel;

import java.util.List;

import javax.inject.Inject;

public class QueryFragment extends Fragment {

	private final ViewModelProvider.Factory _viewModelFactory;
	private SharedQueryViewModel _sharedQueryViewModel;
	private QueryViewModel _queryViewModel;
	private FragmentQueryBinding _binding;

	@Inject
	public QueryFragment(ViewModelProvider.Factory viewModelFactory) {
		_viewModelFactory = viewModelFactory;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentQueryBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		_sharedQueryViewModel = new ViewModelProvider(requireActivity())
				.get(SharedQueryViewModel.class);
		_queryViewModel = new ViewModelProvider(this, _viewModelFactory).get(QueryViewModel.class);
		_sharedQueryViewModel.getSearchUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {

			@Override
			public void onChanged(Uri uri) {
				_queryViewModel.executeQuery(uri);
			}
		});
		_queryViewModel.getAlbumList().observe(getViewLifecycleOwner(),
				new Observer<List<AlbumItemModel>>() {

					@Override
					public void onChanged(List<AlbumItemModel> albumItemModels) {
						if (!albumItemModels.isEmpty()) {
							_binding.querySectionAlbum.setVisibility(View.VISIBLE);
							// todo: implement here
						}
						else {
							_binding.querySectionAlbum.setVisibility(View.GONE);
						}
					}
				});
		_queryViewModel.getArtistList().observe(getViewLifecycleOwner(),
				new Observer<List<ArtistItemModel>>() {

					@Override
					public void onChanged(List<ArtistItemModel> artistItemModels) {
						if (!artistItemModels.isEmpty()) {
							_binding.querySectionArtist.setVisibility(View.VISIBLE);
							_binding.queryRecyclerViewArtist.getRoot()
									.setAdapter(new ArtistAdapter(artistItemModels));
						}
						else {
							_binding.querySectionArtist.setVisibility(View.GONE);
						}
					}
				});
		_queryViewModel.getSongList().observe(getViewLifecycleOwner(),
				new Observer<List<SongItemModel>>() {

					@Override
					public void onChanged(List<SongItemModel> songItemModels) {
						if (!songItemModels.isEmpty()) {
							_binding.querySectionSong.setVisibility(View.VISIBLE);
							_binding.queryRecyclerViewSong.getRoot()
									.setAdapter(new SongAdapter(songItemModels));
						}
						else {
							_binding.querySectionSong.setVisibility(View.GONE);
						}
					}
				});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		_binding = null;
	}
}
