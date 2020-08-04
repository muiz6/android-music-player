package com.muiz6.musicplayer.di.main.home;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.di.main.albums.AlbumComponent;
import com.muiz6.musicplayer.di.main.songs.SongComponent;
import com.muiz6.musicplayer.ui.main.home.HomeViewModel;
import com.muiz6.musicplayer.ui.main.home.TabMediatorStrategy;
import com.muiz6.musicplayer.ui.main.home.albums.AlbumFragment;
import com.muiz6.musicplayer.ui.main.home.songs.SongFragment;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class HomeModule {

	@Binds
	abstract TabLayoutMediator.TabConfigurationStrategy provideTabMediatorStrategy(
			TabMediatorStrategy strategy);

	@Binds
	@IntoMap
	@ClassKey(HomeViewModel.class)
	abstract ViewModel getHomeViewModel(HomeViewModel viewModel);

	@Provides
	@IntoMap
	@ClassKey(SongFragment.class)
	static Fragment getSongFragment(SongComponent.Factory factory) {
		return factory.create().getSongFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(AlbumFragment.class)
	static Fragment provideAlbumFragment(AlbumComponent.Factory factory) {
		return factory.create().getAlbumFragment();
	}
}
