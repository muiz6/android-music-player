package com.muiz6.musicplayer.musicservice.mainui.nowplaying;

import android.content.ComponentName;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.R;

public class NowPlayingActivity extends AppCompatActivity {

    MediaBrowserCompat _mediaBrowser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        NowPlayingActivityMediaBrowserConnectionCallback connectionCallback =
                new NowPlayingActivityMediaBrowserConnectionCallback(this);
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
