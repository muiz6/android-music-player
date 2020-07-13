package com.muiz6.musicplayer.di;

import android.content.ComponentName;
import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.media.MediaConnectionCallback;
import com.muiz6.musicplayer.musicservice.MusicService;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class MediaBrowserModule {

	@FragmentScope
	@Provides
	static MediaBrowserCompat provideMediaBrowser(Context context,
			MediaConnectionCallback connectionCallback) {
		return new MediaBrowserCompat(context,
				new ComponentName(context, MusicService.class),
				connectionCallback,
				null);
	}
}
