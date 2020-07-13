package com.muiz6.musicplayer.ui.main.nowplaying;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.FragmentQueueBinding;

public class QueueFragment extends Fragment {

	private FragmentQueueBinding _binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_queue, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		_binding = FragmentQueueBinding.bind(view);

		// setup toolbar
		final NavController navController = Navigation.findNavController(view);
		NavigationUI.setupWithNavController(
				_binding.queueToolbar, navController);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// avoid memory leaks
		_binding = null;
	}
}
