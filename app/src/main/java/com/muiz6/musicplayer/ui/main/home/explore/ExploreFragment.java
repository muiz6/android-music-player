package com.muiz6.musicplayer.ui.main.home.explore;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.FragmentExploreBinding;

import javax.inject.Inject;

public class ExploreFragment extends Fragment {

	private final FragmentFactory _fragmentFactory;
	private final TabLayoutMediator.TabConfigurationStrategy _tabMediatorStrategy;
	private FragmentExploreBinding _binding;

	@Inject
	public ExploreFragment(FragmentFactory factory,
			TabLayoutMediator.TabConfigurationStrategy tabMediatorStrategy) {
		_fragmentFactory = factory;
		_tabMediatorStrategy = tabMediatorStrategy;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		_binding = FragmentExploreBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// setup toolbar
		((AppCompatActivity) getActivity()).setSupportActionBar(_binding.mainToolbar);
		// final NavController navController = Navigation.findNavController(view);
		// final AppBarConfiguration appBarConfiguration =
		// 		new AppBarConfiguration.Builder(navController.getGraph()).build();
		// NavigationUI.setupWithNavController(_binding.mainToolbar,
		// 		navController,
		// 		appBarConfiguration);

		// setup tab layout
		_binding.mainViewPager.setAdapter(new ExplorePagerAdapter(this,
				getActivity().getClassLoader(),
				_fragmentFactory));
		new TabLayoutMediator(_binding.mainTabLayout,
				_binding.mainViewPager,
				_tabMediatorStrategy).attach();
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_explore, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		final NavController navController = Navigation.findNavController(getView());
		return NavigationUI.onNavDestinationSelected(item, navController)
				|| super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		_binding = null;
	}

	private void _updateFromMetadata(MediaMetadataCompat metadata) {

		// todo: get height not working for some reason
		final int padding = (int) getResources().getDimension(R.dimen.padding_bottom_home_view_pager);
		_binding.mainViewPager.setPadding(_binding.mainViewPager.getPaddingStart(),
				_binding.mainViewPager.getPaddingTop(),
				_binding.mainViewPager.getPaddingEnd(),
				padding);
	}
}
