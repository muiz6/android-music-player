package com.muiz6.musicplayer.ui.main.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.muiz6.musicplayer.ui.main.PlaceholderFragment;
import com.muiz6.musicplayer.ui.main.home.albums.AlbumFragment;
import com.muiz6.musicplayer.ui.main.home.artists.ArtistFragment;
import com.muiz6.musicplayer.ui.main.home.genres.GenreFragment;
import com.muiz6.musicplayer.ui.main.home.songs.SongFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

	private static final Class<?>[] _FRAGMENT_CLASS = {
			SongFragment.class,
			AlbumFragment.class,
			ArtistFragment.class
	};
	private final ClassLoader _classLoader;
	private final FragmentFactory _fragmentFactory;

	public HomePagerAdapter(Fragment fragment,
			ClassLoader classLoader,
			FragmentFactory fragmentFactory) {
		super(fragment);

		_classLoader = classLoader;
		_fragmentFactory = fragmentFactory;
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		if (position <= 2) {
			return _fragmentFactory.instantiate(_classLoader, _FRAGMENT_CLASS[position].getName());
		}
		else if (position == 4) {
			return _fragmentFactory.instantiate(_classLoader, GenreFragment.class.getName());
		}
		else {
			return new PlaceholderFragment();
		}
	}

	@Override
	public int getItemCount() {
		return 5;
	}
}
