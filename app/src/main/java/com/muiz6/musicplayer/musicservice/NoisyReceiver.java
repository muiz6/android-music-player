package com.muiz6.musicplayer.musicservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.media.session.MediaControllerCompat;

import javax.inject.Inject;

public class NoisyReceiver extends BroadcastReceiver {

	private final MediaControllerCompat _controller;

	@Inject
	public NoisyReceiver(MediaControllerCompat controller) {
		_controller = controller;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
			_controller.getTransportControls().pause();
		}
	}
}
