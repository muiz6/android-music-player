package com.muiz6.musicplayer.ui.main.home.library.artists;

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
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.databinding.RvLinearBinding;
import com.muiz6.musicplayer.ui.main.home.library.LibraryFragmentDirections;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class ArtistFragment extends Fragment implements RecyclerView.OnItemTouchListener {

	private final ViewModelProvider.Factory _viewModelFactory;
	private final GestureDetector _gestureDetector;
	private final RecyclerView.ItemDecoration _itemDecoration;
	private ArtistViewModel _viewModel;
	private RvLinearBinding _binding;

	@Inject
	public ArtistFragment(ViewModelProvider.Factory viewModelFactory,
			GestureDetector gestureDetector,
			@Named("List") RecyclerView.ItemDecoration listItemDecoration) {
		_viewModelFactory = viewModelFactory;
		_gestureDetector = gestureDetector;
		_itemDecoration = listItemDecoration;
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
		_binding = RvLinearBinding.inflate(inflater, container, false);
		final RecyclerView recyclerView = _binding.getRoot();
		recyclerView.setAdapter(new ArtistAdapter(
				Collections.<ArtistItemModel>emptyList()));
		recyclerView.addItemDecoration(_itemDecoration);
		recyclerView.addOnItemTouchListener(this);
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

	@Override
	public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
		final View view = rv.findChildViewUnder(e.getX(), e.getY());
		if (view != null && _gestureDetector.onTouchEvent(e)) {
			int index = rv.getChildAdapterPosition(view);
			if (index > 0) {
				final String artistId = _viewModel.getArtistMediaId(index - 1);
				final String artist = _viewModel.getArtistTitle(index);
				final LibraryFragmentDirections.ActionLibraryFragmentToBrowseFragment action =
						LibraryFragmentDirections
								.actionLibraryFragmentToBrowseFragment(artistId, artist);
				final NavController navController = Navigation.findNavController(requireView());
				navController.navigate(action);
			}
		}
		return false;
	}

	@Override
	public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}
