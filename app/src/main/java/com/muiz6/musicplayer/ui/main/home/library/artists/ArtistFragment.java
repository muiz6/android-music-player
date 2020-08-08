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

import com.muiz6.musicplayer.databinding.FragmentListBinding;
import com.muiz6.musicplayer.ui.main.home.library.LibraryFragmentDirections;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class ArtistFragment extends Fragment implements RecyclerView.OnItemTouchListener {

	private final ViewModelProvider.Factory _viewModelFactory;
	private ArtistViewModel _viewModel;
	private FragmentListBinding _binding;
	private GestureDetector _gestureDetector;

	@Inject
	public ArtistFragment(ViewModelProvider.Factory viewModelFactory,
			GestureDetector gestureDetector) {
		_viewModelFactory = viewModelFactory;
		_gestureDetector = gestureDetector;
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
			final String artistId = _viewModel.getArtistMediaId(index);
			final LibraryFragmentDirections.ActionLibraryFragmentToBrowseFragment action =
					LibraryFragmentDirections
							.actionLibraryFragmentToBrowseFragment(artistId);
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
