package com.muiz6.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.muiz6.musicplayer.di.AppComponent;
import com.muiz6.musicplayer.di.DaggerAppComponent;

public class MyApp extends Application {

	public static final String NOTIFICATION_CHANNEL_ID = "MyServiceChannel";
	public static final int NOTIFICATION_ID_PLAYER = 1;

	private AppComponent _appComponent;

	@Override
	public void onCreate() {
		super.onCreate();
		_appComponent = DaggerAppComponent.factory().create();

		_createNotificationChannel();
	}

	public AppComponent getAppComponent() {
		return _appComponent;
	}

	private void _createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			final NotificationChannel serviceChannel = new NotificationChannel(
					NOTIFICATION_CHANNEL_ID,
					"My Service Channel",
					NotificationManager.IMPORTANCE_DEFAULT
			);

			final NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(serviceChannel);
		}
	}
}
