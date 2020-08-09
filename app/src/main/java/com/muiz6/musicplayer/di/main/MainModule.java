package com.muiz6.musicplayer.di.main;

import androidx.fragment.app.Fragment;

import com.muiz6.musicplayer.di.main.albums.AlbumComponent;
import com.muiz6.musicplayer.di.main.artists.ArtistComponent;
import com.muiz6.musicplayer.di.main.browse.BrowseComponent;
import com.muiz6.musicplayer.di.main.genres.GenreComponent;
import com.muiz6.musicplayer.di.main.home.HomeComponent;
import com.muiz6.musicplayer.di.main.library.LibraryComponent;
import com.muiz6.musicplayer.di.main.nowplaying.PlayerComponent;
import com.muiz6.musicplayer.di.main.songs.SongComponent;
import com.muiz6.musicplayer.ui.main.home.HomeFragment;
import com.muiz6.musicplayer.ui.main.home.browse.BrowseFragment;
import com.muiz6.musicplayer.ui.main.nowplaying.PlayerFragment;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {HomeComponent.class,
		PlayerComponent.class,
		SongComponent.class,
		AlbumComponent.class,
		ArtistComponent.class,
		GenreComponent.class,
		LibraryComponent.class,
		BrowseComponent.class})
public abstract class MainModule {

	@Provides
	@IntoMap
	@ClassKey(HomeFragment.class)
	static Fragment provideHomeFragment(HomeComponent.Factory factory) {
		return factory.create().getHomeFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(PlayerFragment.class)
	static Fragment providePlayerFragment(PlayerComponent.Factory factory) {
		return factory.create().getPlayerFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(BrowseFragment.class)
	static Fragment provideBrowseFragment(BrowseComponent.Factory factory) {
		return factory.create().getBrowseFragment();
	}
}
