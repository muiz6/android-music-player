package com.muiz6.musicplayer.ui.main.home.browse;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.muiz6.musicplayer.databinding.FragmentBrowseBinding;
import com.muiz6.musicplayer.ui.main.QueryViewModel;

/**
 * fragment to browse simple list items eg. artists, genre etc.
 */
public class BrowseFragment extends Fragment {

	private FragmentBrowseBinding _binding;
	private QueryViewModel _queryViewModel;
	private BrowseFragmentArgs _arguments;

	public BrowseFragment() {}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentBrowseBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		_queryViewModel = new ViewModelProvider(requireActivity()).get(QueryViewModel.class);
		_arguments = BrowseFragmentArgs.fromBundle(getArguments());

		// set toolbar
		final NavController navController = Navigation.findNavController(requireView());
		NavigationUI.setupWithNavController(_binding.browseToolbar, navController);
		_binding.browseToolbar.setTitle(_arguments.getQuery());

		// send query to query fragment
		final Uri.Builder uriBuilder = new Uri.Builder();
		final String queryTypeId = _arguments.getQueryTypeId();
		if (queryTypeId.equals(QueryViewModel.TYPE_ARTIST)
				|| queryTypeId.equals(QueryViewModel.TYPE_GENRE)) {
			uriBuilder.appendQueryParameter(QueryViewModel.KEY_TYPE, queryTypeId)
					.appendQueryParameter(QueryViewModel.KEY_VALUE, _arguments.getQuery());
			_queryViewModel.setSearchUri(uriBuilder.build());
		}
		else {
			throw new IllegalArgumentException("Invalid query type id!");
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// forget query
		_queryViewModel.setSearchUri(Uri.EMPTY);
		_binding = null;
	}
}
