package com.muiz6.musicplayer.ui.main.home.library;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.muiz6.musicplayer.ui.main.home.library.albums.AlbumFragment;
import com.muiz6.musicplayer.ui.main.home.library.artists.ArtistFragment;
import com.muiz6.musicplayer.ui.main.home.library.genres.GenreFragment;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongFragment;

public class LibraryPagerAdapter extends FragmentStateAdapter {

	private static final Class<?>[] _FRAGMENT_CLASSES = {
			SongFragment.class,
			AlbumFragment.class,
			ArtistFragment.class,
			GenreFragment.class
	};
	private final ClassLoader _classLoader;
	private final FragmentFactory _fragmentFactory;

	public LibraryPagerAdapter(Fragment fragment,
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
