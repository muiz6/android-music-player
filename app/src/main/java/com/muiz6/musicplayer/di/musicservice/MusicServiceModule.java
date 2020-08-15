package com.muiz6.musicplayer.di.musicservice;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.navigation.NavDeepLinkBuilder;

import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.MyApp;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.di.scope.ActivityScope;
import com.muiz6.musicplayer.musicservice.NoisyReceiver;
import com.muiz6.musicplayer.musicservice.NotificationDescriptionAdapter;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class MusicServiceModule {

	@Binds
	@Named("NoisyReceiver")
	abstract BroadcastReceiver provideNoisyReceiver(NoisyReceiver receiver);

	@Provides
	@ActivityScope
	static MediaSessionCompat provideMediaSession(@Named("Application") Context context,
			@Named("Notification") PendingIntent pIntent) {
		final MediaSessionCompat session = new MediaSessionCompat(context, "AppMediaSession");
		session.setSessionActivity(pIntent);
		return session;
	}

	@Provides
	@ActivityScope
	static MediaControllerCompat provideMediaController(MediaSessionCompat session) {
		return session.getController();
	}

	@Provides
	static MediaSessionConnector provideMediaSessionConnector(MediaSessionCompat session) {
		 return new MediaSessionConnector(session);
	}

	// todo: maybe its better to replace @Named with multibinding
	@Provides
	@Named("Notification")
	static PendingIntent provideNotificationIntent(@Named("Application") Context context) {
		return new NavDeepLinkBuilder(context)
				.setGraph(R.navigation.navigation_home)
				.setDestination(R.id.libraryFragment)
				.createPendingIntent();
	}

	@Provides
	static PlayerNotificationManager provideNotificationManager(
			@Named("Application") Context context,
			NotificationDescriptionAdapter adapter) {
		final PlayerNotificationManager manager = new PlayerNotificationManager(context,
				MyApp.NOTIFICATION_CHANNEL_ID,
				MyApp.NOTIFICATION_ID_PLAYER,
				adapter);
		manager.setColor(Color.BLACK);
		manager.setColorized(true);
		manager.setUseNavigationActionsInCompactView(true);
		manager.setSmallIcon(R.drawable.ic_play_circle_filled);

		// hide fast forward and rewind buttons
		manager.setFastForwardIncrementMs(0);
		manager.setRewindIncrementMs(0);
		return manager;
	}
}
