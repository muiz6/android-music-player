package com.muiz6.musicplayer.di.main;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.di.main.albums.AlbumComponent;
import com.muiz6.musicplayer.di.main.artists.ArtistComponent;
import com.muiz6.musicplayer.di.main.browse.BrowseComponent;
import com.muiz6.musicplayer.di.main.genres.GenreComponent;
import com.muiz6.musicplayer.di.main.home.HomeComponent;
import com.muiz6.musicplayer.di.main.library.LibraryComponent;
import com.muiz6.musicplayer.di.main.queue.QueueComponent;
import com.muiz6.musicplayer.di.main.songs.SongComponent;
import com.muiz6.musicplayer.ui.main.home.HomeFragment;
import com.muiz6.musicplayer.ui.main.home.browse.BrowseFragment;
import com.muiz6.musicplayer.ui.main.home.queue.QueueFragment;
import com.muiz6.musicplayer.ui.recyclerviewutil.GridItemDecoration;
import com.muiz6.musicplayer.ui.recyclerviewutil.ListItemDecoration;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {HomeComponent.class,
		SongComponent.class,
		AlbumComponent.class,
		ArtistComponent.class,
		GenreComponent.class,
		LibraryComponent.class,
		BrowseComponent.class,
		QueueComponent.class})
public abstract class MainModule {

	@Provides
	@IntoMap
	@ClassKey(HomeFragment.class)
	static Fragment provideHomeFragment(HomeComponent.Factory factory) {
		return factory.create().getHomeFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(BrowseFragment.class)
	static Fragment provideBrowseFragment(BrowseComponent.Factory factory) {
		return factory.create().getBrowseFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(QueueFragment.class)
	static Fragment provideQueueFragment(QueueComponent.Factory factory) {
		return factory.create().getQueueFragment();
	}

	@Provides
	@Named("List")
	static RecyclerView.ItemDecoration provideListItemDecoration() {
		return new ListItemDecoration(R.dimen.margin_m);
	}

	@Provides
	@Named("Grid")
	static RecyclerView.ItemDecoration provideGridItemDecoration() {
		return new GridItemDecoration(R.dimen.margin_m);
	}
}
