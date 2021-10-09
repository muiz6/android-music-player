package com.muiz6.musicplayer.ui.main.home.queue;

import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.muiz6.musicplayer.databinding.FragmentListBinding;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongAdapter;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongItemModel;

import java.util.List;

import javax.inject.Inject;

public class QueueFragment extends Fragment {

	private final ViewModelProvider.Factory _viewModelFactory;
	private QueueViewModel _viewModel;
	private FragmentListBinding _binding;

	@Inject
	public QueueFragment(ViewModelProvider.Factory viewModelFactory) {
		_viewModelFactory = viewModelFactory;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(QueueViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentListBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// setup toolbar
		final NavController navController = Navigation.findNavController(view);
		NavigationUI.setupWithNavController(_binding.listToolbar, navController);

		_viewModel.getQueue().observe(getViewLifecycleOwner(), new Observer<List<SongItemModel>>() {

			@Override
			public void onChanged(List<SongItemModel> songItemModels) {
				_binding.listBody.getRoot().setAdapter(new SongAdapter(songItemModels));
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// to avoid memory leaks
		_binding = null;
	}
}
