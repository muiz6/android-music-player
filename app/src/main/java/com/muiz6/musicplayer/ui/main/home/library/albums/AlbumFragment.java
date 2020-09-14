package com.muiz6.musicplayer.ui.main.home.library.albums;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.databinding.RvGridBinding;
import com.muiz6.musicplayer.ui.main.home.library.LibraryFragmentDirections;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class AlbumFragment extends Fragment implements RecyclerView.OnItemTouchListener {

	private final ViewModelProvider.Factory _viewModelFactory;
	private final GestureDetector _gestureDetector;
	private final RecyclerView.ItemDecoration _itemDecoration;
	private AlbumViewModel _viewModel;
	private RvGridBinding _binding;

	@Inject
	public AlbumFragment(ViewModelProvider.Factory viewModelFactory,
			GestureDetector gestureDetector,
			@Named("Grid") RecyclerView.ItemDecoration gridItemDecoration) {
		_viewModelFactory = viewModelFactory;
		_gestureDetector = gestureDetector;
		_itemDecoration = gridItemDecoration;
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
		_binding = RvGridBinding.inflate(inflater, container, false);
		final RecyclerView recyclerView = _binding.getRoot();
		recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 3));
		recyclerView.setAdapter(new AlbumAdapter(Collections.<AlbumItemModel>emptyList()));
		recyclerView.addItemDecoration(_itemDecoration);
		recyclerView.addOnItemTouchListener(this);
		return recyclerView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		_viewModel.getAlbumList().observe(getViewLifecycleOwner(),
				new Observer<List<AlbumItemModel>>() {

					@Override
					public void onChanged(List<AlbumItemModel> albumList) {
						_binding.getRoot().setAdapter(new AlbumAdapter(albumList));
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
			final String albumId = _viewModel.getAlbumMediaId(index);
			final String album = _viewModel.getAlbumTitle(index);
			final LibraryFragmentDirections.ActionLibraryFragmentToBrowseFragment action =
					LibraryFragmentDirections
							.actionLibraryFragmentToBrowseFragment(albumId, album);
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
