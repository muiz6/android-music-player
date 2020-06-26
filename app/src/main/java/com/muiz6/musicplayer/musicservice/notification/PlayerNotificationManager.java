package com.muiz6.musicplayer.musicservice.notification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.muiz6.musicplayer.BuildConfig;
import com.muiz6.musicplayer.R;

import static com.muiz6.musicplayer.App.NOTIFICATION_CHANNEL_ID;

public class PlayerNotificationManager extends BroadcastReceiver {

	private static final String ACTION_NOTIFICATION_BTN =
			BuildConfig.APPLICATION_ID + ".NOTIFICATION_BUTTON";
	private final NotificationCompat.Builder _builder;
	private final MediaBrowserServiceCompat _service;
	private final MediaControllerCompat.Callback _callback;
	private final MediaControllerCompat _controller;
	private final MediaSessionCompat _session;

	public PlayerNotificationManager(MediaBrowserServiceCompat service,
			MediaSessionCompat session) {
		_service = service;
		_session = session;
		_controller = session.getController();
		_builder = new NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID);

		final Bitmap bmp = BitmapFactory.decodeResource(_service.getResources(),
				R.drawable.artwork_placeholder);

		// Add the metadata for the currently playing track
		_builder.setSmallIcon(R.drawable.ic_face_black_24dp)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
				.setLargeIcon(bmp)

				// Enable launching the player by clicking the notification
				.setContentIntent(_controller.getSessionActivity())

				// Stop the service when the notification is swiped away
				.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(_service,
						PlaybackStateCompat.ACTION_STOP))

				// Make the transport controls visible on the lockscreen
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

				// Add an app icon and set its accent color
				// Be careful about the color
				.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
						.setMediaSession(_service.getSessionToken())
						.setShowActionsInCompactView(0, 1, 2)
						// Add a cancel button
						.setShowCancelButton(true)
						.setCancelButtonIntent(MediaButtonReceiver
								.buildMediaButtonPendingIntent(_service,
										PlaybackStateCompat.ACTION_STOP)))
				.addAction(new NotificationCompat.Action(R.drawable.ic_skip_previous_black_24dp,
						"skip_to_previous",
						_getPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
				.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp,
						"play_pause",
						_getPendingIntent(PlaybackStateCompat.ACTION_PLAY_PAUSE)))
				.addAction(new NotificationCompat.Action(R.drawable.ic_skip_next_black_24dp,
						"skip_to_next",
						_getPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

		// attach notification to controller callback
		_callback = new _ControllerCallback(_service, _builder);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		_controller.getTransportControls().pause();
		intent.setAction(Intent.ACTION_MEDIA_BUTTON);
		MediaButtonReceiver.handleIntent(_session, intent);
	}

	public void start() {
		_controller.registerCallback(_callback);
		IntentFilter filter = new IntentFilter(ACTION_NOTIFICATION_BTN);
		_service.registerReceiver(this, filter);
	}

	/**
	 * Call when done with player notification.
	 * Unregisters callback with media session controller.
	 */
	public void stop() {
		_controller.unregisterCallback(_callback);
		_service.unregisterReceiver(this);
		_service.stopForeground(true);
	}

	// make this receiver receive the broadcast pending intents
	// convert them to MediaButtonReceiver compatible intents
	// by changing action to ACTION_MEDIA_BUTTON
	// call MediaButtonReceiver#handleIntent(), in onReceive(),
	// to let it handle intent automatically
	@Nullable
	private PendingIntent _getPendingIntent(long action) {
		int keyCode = PlaybackStateCompat.toKeyCode(action);
		if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
			Log.w("PlayerNotificationMgr",
					"Cannot build a media button pending intent with the given action: " + action);
			return null;
		}

		// create intent for MediaButtonReceiver (copied from its source code)
		Intent intent = new Intent(ACTION_NOTIFICATION_BTN);
		intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
		return PendingIntent.getBroadcast(_service, keyCode, intent, 0);
	}
}
