package com.muiz6.musicplayer.ui.main.home.browse;

import android.os.Bundle;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.databinding.FragmentBrowseBinding;
import com.muiz6.musicplayer.ui.main.home.library.albums.AlbumAdapter;
import com.muiz6.musicplayer.ui.main.home.library.albums.AlbumItemModel;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongAdapter;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongItemModel;

import java.util.List;

import javax.inject.Inject;

/**
 * fragment to browse simple list items eg. artists, genre etc.
 */
public class BrowseFragment extends Fragment implements RecyclerView.OnItemTouchListener {

	private final ViewModelProvider.Factory _viewModelFactory;
	private final  GestureDetector _gestureDetector;
	private BrowseViewModel _browseViewModel;
	private FragmentBrowseBinding _binding;
	private BrowseFragmentArgs _arguments;

	@Inject
	public BrowseFragment(ViewModelProvider.Factory viewModelFactory, GestureDetector gestureDetector) {
		_viewModelFactory = viewModelFactory;
		_gestureDetector = gestureDetector;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentBrowseBinding.inflate(inflater, container, false);
		final RecyclerView _albumRecyclerView = _binding.browseRecyclerViewAlbum.getRoot();
		_albumRecyclerView.addOnItemTouchListener(this);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		_arguments = BrowseFragmentArgs.fromBundle(requireArguments());

		// set toolbar
		final NavController navController = Navigation.findNavController(requireView());
		NavigationUI.setupWithNavController(_binding.browseToolbar, navController);

		// send id to viewmodel
		_browseViewModel = new ViewModelProvider(this, _viewModelFactory).get(BrowseViewModel.class);
		_browseViewModel.setParentMediaId(_arguments.getParentMediaId());

		// get fragment title
		_browseViewModel.getFragmentTitle().observe(getViewLifecycleOwner(),
				new Observer<String>() {

					@Override
					public void onChanged(String title) {
						if (title != null) {
							_binding.browseToolbar.setTitle(title);
						}
					}
				});

		// setup album grid layout
		_binding.browseRecyclerViewAlbum.getRoot()
				.setLayoutManager(new GridLayoutManager(requireContext(), 3));

		// getData
		_browseViewModel.getAlbumList().observe(getViewLifecycleOwner(),
				new Observer<List<AlbumItemModel>>() {

					@Override
					public void onChanged(List<AlbumItemModel> albumItemModels) {
						final RecyclerView rv = _binding.browseRecyclerViewAlbum.getRoot();
						if (!albumItemModels.isEmpty()) {
							rv.setVisibility(View.VISIBLE);
							rv.setAdapter(new AlbumAdapter(albumItemModels));
						}
						else {
							rv.setVisibility(View.GONE);
						}
					}
				});
		_browseViewModel.getSongList().observe(getViewLifecycleOwner(),
				new Observer<List<SongItemModel>>() {

					@Override
					public void onChanged(List<SongItemModel> songItemModels) {
						final RecyclerView rv = _binding.browseRecyclerViewSong.getRoot();
						if (!songItemModels.isEmpty()) {
							rv.setVisibility(View.VISIBLE);
							rv.setAdapter(new SongAdapter(songItemModels));
						}
						else {
							rv.setVisibility(View.GONE);
						}
					}
				});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// forget query
		// _queryViewModel.setSearchUri(Uri.EMPTY);
		_binding = null;
	}

	@Override
	public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
		final View view = rv.findChildViewUnder(e.getX(), e.getY());
		if (view != null && _gestureDetector.onTouchEvent(e)) {
			int index = rv.getChildAdapterPosition(view);
			final String albumId = _browseViewModel.getAlbumMediaId(index);
			BrowseFragmentDirections.ActionBrowseFragmentToBrowseFragment action =
					BrowseFragmentDirections.actionBrowseFragmentToBrowseFragment(albumId);
			final NavController navController = Navigation.findNavController(requireView());
			navController.navigate(action);
		}
		return false;
	}

	@Override
	public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}
