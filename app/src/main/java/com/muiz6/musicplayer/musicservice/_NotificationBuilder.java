package com.muiz6.musicplayer.musicservice;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.muiz6.musicplayer.R;

import static com.muiz6.musicplayer.App.CHANNEL_ID;

class _NotificationBuilder extends NotificationCompat.Builder {

	public static final int MUSIC_NOTIFICATION_ID = 1;

	private final MediaSessionCompat _session;
	private final MediaBrowserServiceCompat _service;

	public _NotificationBuilder(MediaBrowserServiceCompat service,
			MediaSessionCompat session) {
		super(service, CHANNEL_ID);
		_session = session;
		_service = service;

		MediaControllerCompat controller = _session.getController();

		Bitmap bmp = BitmapFactory.decodeResource(_service.getResources(),
				R.drawable.artwork_placeholder);

				// Add the metadata for the currently playing track
		setSmallIcon(R.drawable.ic_face_black_24dp);
		setContentTitle("Title");
		setPriority(NotificationCompat.PRIORITY_DEFAULT);
		setDefaults(NotificationCompat.DEFAULT_LIGHTS);
		setContentText("Artist");
		setSubText("Album");
		setLargeIcon(bmp);

				// Enable launching the player by clicking the notification
		setContentIntent(controller.getSessionActivity());

				// Stop the service when the notification is swiped away
		setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(_service,
				PlaybackStateCompat.ACTION_STOP));

				// Make the transport controls visible on the lockscreen
		setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

				// Add an app icon and set its accent color
				// Be careful about the color
		setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
				.setMediaSession(_session.getSessionToken())
				.setShowActionsInCompactView(0, 1, 2)
				// Add a cancel button
				.setShowCancelButton(true)
				.setCancelButtonIntent(MediaButtonReceiver
						.buildMediaButtonPendingIntent(_service,
								PlaybackStateCompat.ACTION_STOP)));
		addAction(new NotificationCompat.Action(R.drawable.ic_skip_previous_black_24dp,
				"skip_to_previous",
				MediaButtonReceiver.buildMediaButtonPendingIntent(_service,
						PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));

				// Add a pause button
		addAction(new NotificationCompat.Action(R.drawable.ic_pause_black_24dp,
				"pause",
				MediaButtonReceiver.buildMediaButtonPendingIntent(_service,
						PlaybackStateCompat.ACTION_PLAY_PAUSE)));

		addAction(new NotificationCompat.Action(R.drawable.ic_skip_next_black_24dp,
				"skip_to_next",
				MediaButtonReceiver.buildMediaButtonPendingIntent(_service,
						PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));
	}
}
