package com.muiz6.musicplayer.di.main.library;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.di.main.albums.AlbumComponent;
import com.muiz6.musicplayer.di.main.artists.ArtistComponent;
import com.muiz6.musicplayer.di.main.genres.GenreComponent;
import com.muiz6.musicplayer.di.main.songs.SongComponent;
import com.muiz6.musicplayer.ui.main.home.library.LibraryViewModel;
import com.muiz6.musicplayer.ui.main.home.library.TabMediatorStrategy;
import com.muiz6.musicplayer.ui.main.home.library.albums.AlbumFragment;
import com.muiz6.musicplayer.ui.main.home.library.artists.ArtistFragment;
import com.muiz6.musicplayer.ui.main.home.library.genres.GenreFragment;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongFragment;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class LibraryModule {

	@Binds
	abstract TabLayoutMediator.TabConfigurationStrategy provideTabMediatorStrategy(
			TabMediatorStrategy strategy);

	@Binds
	@IntoMap
	@ClassKey(LibraryViewModel.class)
	abstract ViewModel getLibraryViewModel(LibraryViewModel viewModel);

	@Provides
	@IntoMap
	@ClassKey(SongFragment.class)
	static Fragment getSongFragment(SongComponent.Factory factory) {
		return factory.create().getSongFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(ArtistFragment.class)
	static Fragment provideArtistFragment(ArtistComponent.Factory factory) {
		return factory.create().getArtistFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(GenreFragment.class)
	static Fragment provideGenreFragment(GenreComponent.Factory factory) {
		return factory.create().getGenreFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(AlbumFragment.class)
	static Fragment provideAlbumFragment(AlbumComponent.Factory factory) {
		return factory.create().getAlbumFragment();
	}
}
