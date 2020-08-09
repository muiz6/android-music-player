package com.muiz6.musicplayer.di.main;

import android.content.Context;

import com.muiz6.musicplayer.di.FragmentFactoryModule;
import com.muiz6.musicplayer.di.GestureDetectorModule;
import com.muiz6.musicplayer.di.ViewModelFactoryModule;
import com.muiz6.musicplayer.di.main.albums.AlbumComponent;
import com.muiz6.musicplayer.di.main.artists.ArtistComponent;
import com.muiz6.musicplayer.di.main.browse.BrowseComponent;
import com.muiz6.musicplayer.di.main.genres.GenreComponent;
import com.muiz6.musicplayer.di.main.home.HomeComponent;
import com.muiz6.musicplayer.di.main.library.LibraryComponent;
import com.muiz6.musicplayer.di.main.nowplaying.PlayerComponent;
import com.muiz6.musicplayer.di.main.songs.SongComponent;
import com.muiz6.musicplayer.di.scope.ActivityScope;
import com.muiz6.musicplayer.ui.main.MainActivity;

import dagger.BindsInstance;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {MainModule.class,
		FragmentFactoryModule.class,
		ViewModelFactoryModule.class,
		GestureDetectorModule.class})
public interface MainComponent {

	void inject(MainActivity activity);

	HomeComponent.Factory getHomeComponent();

	SongComponent.Factory getSongComponent();

	AlbumComponent.Factory getAlbumComponent();

	ArtistComponent.Factory getArtistComponent();

	GenreComponent.Factory getGenreComponent();

	PlayerComponent.Factory getPlayerComponent();

	LibraryComponent.Factory getExploreComponent();

	BrowseComponent.Factory getBrowseComponent();

	@Subcomponent.Factory
	interface Factory {

		MainComponent create(@BindsInstance Context context);
	}
}
