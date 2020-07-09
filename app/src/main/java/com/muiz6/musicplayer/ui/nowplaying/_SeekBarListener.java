package com.muiz6.musicplayer.ui.nowplaying;

import android.app.Activity;
import android.support.v4.media.session.MediaControllerCompat;
import android.widget.SeekBar;

public class _SeekBarListener implements SeekBar.OnSeekBarChangeListener {

	private MediaControllerCompat.TransportControls _transportControls;

	_SeekBarListener(MediaControllerCompat.TransportControls controls) {
		_transportControls = controls;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			_transportControls.seekTo(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
}
