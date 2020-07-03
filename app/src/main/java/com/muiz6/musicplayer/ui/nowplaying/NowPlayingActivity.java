package com.muiz6.musicplayer.ui.nowplaying;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.ui.MyConnectionCallback;

public class NowPlayingActivity extends AppCompatActivity {

	private FloatingActionButton _fab;
	private MediaBrowserCompat _mediaBrowser;
	private MyConnectionCallback _connectionCallback;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_now_playing);

		_fab = findViewById(R.id.now_playing_fab);

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
		if (view == _fab) {
			final Intent intent = new Intent(this, QueueActivity.class);
			startActivity(intent);
		}
	}
}
