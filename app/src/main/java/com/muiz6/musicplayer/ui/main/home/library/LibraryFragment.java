package com.muiz6.musicplayer.ui.main.home.library;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.FragmentLibraryBinding;

import javax.inject.Inject;

public class LibraryFragment extends Fragment {

	private final FragmentFactory _fragmentFactory;
	private final TabLayoutMediator.TabConfigurationStrategy _tabMediatorStrategy;
	private final ViewModelProvider.Factory _viewModelFactory;
	private FragmentLibraryBinding _binding;
	private LibraryViewModel _viewModel;

	@Inject
	public LibraryFragment(FragmentFactory factory,
			ViewModelProvider.Factory viewModelFactory,
			TabLayoutMediator.TabConfigurationStrategy tabMediatorStrategy) {
		_fragmentFactory = factory;
		_viewModelFactory = viewModelFactory;
		_tabMediatorStrategy = tabMediatorStrategy;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // better to call here to avoid unexpected behaviour
		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(LibraryViewModel.class);
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		getChildFragmentManager().setFragmentFactory(_fragmentFactory);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentLibraryBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// setup toolbar
		((AppCompatActivity) getActivity()).setSupportActionBar(_binding.libraryToolbar);
		// final NavController navController = Navigation.findNavController(view);
		// final AppBarConfiguration appBarConfiguration =
		// 		new AppBarConfiguration.Builder(navController.getGraph()).build();
		// NavigationUI.setupWithNavController(_binding.mainToolbar,
		// 		navController,
		// 		appBarConfiguration);

		// setup tab layout
		_binding.libraryViewPager.setAdapter(new LibraryPagerAdapter(this,
				getActivity().getClassLoader(),
				_fragmentFactory));
		new TabLayoutMediator(_binding.libraryTabLayout,
				_binding.libraryViewPager,
				_tabMediatorStrategy).attach();
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_library, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.action_rescan_library) {
			_viewModel.onRescanLibraryAction();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		_binding = null;
	}

	// private void _updateFromMetadata(MediaMetadataCompat metadata) {
	//
	// 	// todo: get height not working for some reason
	// 	final int padding = (int) getResources().getDimension(R.dimen.padding_bottom_home_view_pager);
	// 	_binding.mainViewPager.setPadding(_binding.mainViewPager.getPaddingStart(),
	// 			_binding.mainViewPager.getPaddingTop(),
	// 			_binding.mainViewPager.getPaddingEnd(),
	// 			padding);
	// }
}
