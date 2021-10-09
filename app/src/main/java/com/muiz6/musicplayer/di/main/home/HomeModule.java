package com.muiz6.musicplayer.di.main.home;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.di.main.library.LibraryComponent;
import com.muiz6.musicplayer.ui.main.home.HomeViewModel;
import com.muiz6.musicplayer.ui.main.home.library.LibraryFragment;

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
	@ClassKey(LibraryFragment.class)
	static Fragment getSongFragment(LibraryComponent.Factory factory) {
		return factory.create().getExploreFragment();
	}
}
