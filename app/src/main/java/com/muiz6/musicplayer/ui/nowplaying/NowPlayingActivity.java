package com.muiz6.musicplayer.ui.nowplaying;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.muiz6.musicplayer.databinding.ActivityNowPlayingBinding;
import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.ui.MyConnectionCallback;

public class NowPlayingActivity extends AppCompatActivity {

	private ActivityNowPlayingBinding _binding;
	private MediaBrowserCompat _mediaBrowser;
	private MyConnectionCallback _connectionCallback;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_binding = ActivityNowPlayingBinding.inflate(getLayoutInflater());
		setContentView(_binding.getRoot());

		final MediaControllerCompat.Callback controllerCallback =
				new _MediaControllerCallback(this);
		_connectionCallback = new _ConnectionCallback(this, controllerCallback);
		_mediaBrowser = new MediaBrowserCompat(this,
				new ComponentName(this, MusicService.class),
				_connectionCallback,
				null);
	}

	@Override
	protected void onStart() {
		super.onStart();

		_connectionCallback.setMediaBrowser(_mediaBrowser);
		_mediaBrowser.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		_mediaBrowser.disconnect();
	}

	public void onClick(View view) {
		if (view == _binding.nowPlayingFab) {
			final Intent intent = new Intent(this, QueueActivity.class);
			startActivity(intent);
		}
	}
}
