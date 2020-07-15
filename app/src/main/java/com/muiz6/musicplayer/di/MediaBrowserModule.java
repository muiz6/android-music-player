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

	@Provides
	static ComponentName provideComponentName(Context context) {
		return new ComponentName(context, MusicService.class);
	}

	@FragmentScope
	@Provides
	static MediaBrowserCompat provideMediaBrowser(Context context,
			MediaConnectionCallback connectionCallback,
			ComponentName componentName) {
		return new MediaBrowserCompat(context,
				componentName,
				connectionCallback,
				null);
	}
}
