package com.muiz6.musicplayer.di.main.home;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.di.main.explore.ExploreComponent;
import com.muiz6.musicplayer.ui.main.home.HomeViewModel;
import com.muiz6.musicplayer.ui.main.home.explore.ExploreFragment;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class HomeModule {

	@Binds
	@IntoMap
	@ClassKey(HomeViewModel.class)
	abstract ViewModel getHomeViewModel(HomeViewModel viewModel);

	@Provides
	@IntoMap
	@ClassKey(ExploreFragment.class)
	static Fragment getSongFragment(ExploreComponent.Factory factory) {
		return factory.create().getExploreFragment();
	}
}
