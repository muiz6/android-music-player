package com.muiz6.musicplayer.ui.main.home.explore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.muiz6.musicplayer.ui.main.home.explore.albums.AlbumFragment;
import com.muiz6.musicplayer.ui.main.home.explore.artists.ArtistFragment;
import com.muiz6.musicplayer.ui.main.home.explore.genres.GenreFragment;
import com.muiz6.musicplayer.ui.main.home.explore.songs.SongFragment;

public class ExplorePagerAdapter extends FragmentStateAdapter {

	private static final Class<?>[] _FRAGMENT_CLASSES = {
			SongFragment.class,
			AlbumFragment.class,
			ArtistFragment.class,
			GenreFragment.class
	};
	private final ClassLoader _classLoader;
	private final FragmentFactory _fragmentFactory;

	public ExplorePagerAdapter(Fragment fragment,
			ClassLoader classLoader,
			FragmentFactory fragmentFactory) {
		super(fragment);

		_classLoader = classLoader;
		_fragmentFactory = fragmentFactory;
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		return _fragmentFactory.instantiate(_classLoader, _FRAGMENT_CLASSES[position].getName());
	}

	@Override
	public int getItemCount() {
		return _FRAGMENT_CLASSES.length;
	}
}
