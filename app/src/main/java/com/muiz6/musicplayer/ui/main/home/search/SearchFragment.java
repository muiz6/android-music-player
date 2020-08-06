package com.muiz6.musicplayer.ui.main.home.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.muiz6.musicplayer.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

	private FragmentSearchBinding _binding;

	public SearchFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentSearchBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// setup toolbar
		final NavController navController = Navigation.findNavController(view);
		final AppBarConfiguration appBarConfiguration =
				new AppBarConfiguration.Builder(navController.getGraph()).build();
		NavigationUI.setupWithNavController(_binding.searchToolbar,
				navController,
				appBarConfiguration);
		_binding.searchSearchView.requestFocus();

		// show soft keyboard
		// todo: does not work
		// final InputMethodManager imm = (InputMethodManager) getActivity()
		// 		.getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.showSoftInput(_binding.searchSearchView, InputMethodManager.SHOW_IMPLICIT);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		_binding = null;
	}
}
