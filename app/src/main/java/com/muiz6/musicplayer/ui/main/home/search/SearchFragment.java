package com.muiz6.musicplayer.ui.main.home.search;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.muiz6.musicplayer.databinding.FragmentSearchBinding;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.QueryViewModel;

@FragmentScope
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

	private final Uri.Builder _uriBuilder = new Uri.Builder();
	private QueryViewModel _queryViewModel;
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

		_queryViewModel = new ViewModelProvider(requireActivity()).get(QueryViewModel.class);
		_binding.searchSearchView.setOnQueryTextListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		_binding = null;

		// forget query to avoid searching for old query on revisit
		_queryViewModel.setSearchUri(Uri.EMPTY);
	}

	@Override
	public boolean onQueryTextSubmit(String s) {
		_uriBuilder.clearQuery();
		_uriBuilder.appendQueryParameter(QueryViewModel.KEY_TYPE, QueryViewModel.TYPE_QUERY);
		_uriBuilder.appendQueryParameter(QueryViewModel.KEY_VALUE, s);
		_queryViewModel.setSearchUri(_uriBuilder.build());
		return false;
	}

	@Override
	public boolean onQueryTextChange(String s) {
		return false;
	}
}
