package com.muiz6.musicplayer.di.main.explore;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.di.main.albums.AlbumComponent;
import com.muiz6.musicplayer.di.main.songs.SongComponent;
import com.muiz6.musicplayer.ui.main.home.library.TabMediatorStrategy;
import com.muiz6.musicplayer.ui.main.home.library.albums.AlbumFragment;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongFragment;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class ExploreModule {

	@Binds
	abstract TabLayoutMediator.TabConfigurationStrategy provideTabMediatorStrategy(
			TabMediatorStrategy strategy);

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
