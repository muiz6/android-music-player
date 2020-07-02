package com.muiz6.musicplayer.ui.nowplaying;

import android.content.ComponentName;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.musicservice.MusicService;

public class NowPlayingActivity extends AppCompatActivity {

    MediaBrowserCompat _mediaBrowser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        _ConnectionCallback connectionCallback =
                new _ConnectionCallback(this);
        _mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class),
                connectionCallback,
                null);

    }

    @Override
    protected void onStart() {
        super.onStart();

        _mediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        _mediaBrowser.disconnect();
    }

    public MediaBrowserCompat getMediaBrowser() {
        return _mediaBrowser;
    }
}
