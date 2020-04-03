package com.muiz6.musicplayer.callbacks;

import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.MainActivity;

public class MainActivityMediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

    private static final String _TAG = "mbConnectionCallback";
    private final MainActivity _activity;
    private final MediaControllerCompat.Callback _controllerCallback;
    private MediaBrowserCompat _mediaBrowser;

    public MainActivityMediaBrowserConnectionCallback(MainActivity activity) {

        _activity = activity;
        _controllerCallback = activity.getMediaControllerCallback();
    }

    @Override
    public void onConnected() {
        Log.d(_TAG,"Connected!");

        // Get the token for the MediaSession
        MediaSessionCompat.Token token = _mediaBrowser.getSessionToken();

        // Create a MediaController
        try {
            MediaControllerCompat mediaController =
                new MediaControllerCompat(_activity, token);

            // Save the controller for using anywhere
            MediaControllerCompat.setMediaController(_activity, mediaController);
        }
        catch (RemoteException e) {
            Log.e("mbConnectionCallback", "Error creating controller", e);
        }

        _buildTransportControls();
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();

        Log.d(_TAG, "onConnectionSuspended: suspended ;(");
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();

        Log.d(_TAG, "connection failed!");
    }

    private void _buildTransportControls() {

        // Grab the view for the play/pause button
        ImageButton playPauseBtn = _activity.findViewById(R.id.main_bottom_appbar_btn_play);

        // Attach a listener to the button
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly

                Log.d(_TAG,"playpause button pressed");

                int pbState = MediaControllerCompat.getMediaController(_activity)
                    .getPlaybackState()
                    .getState();

                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(_activity)
                        .getTransportControls()
                        .pause();
                }
                // else if(pbState == PlaybackStateCompat.STATE_STOPPED) {
                //
                // }
                else {
                    // ArrayList<SongDataModel> data = Repository.getInstance(_activity)
                    //     .getSongList().getValue();
                    //
                    // int rand = ThreadLocalRandom.current().nextInt(0, data.size());
                    // Uri path = Uri.parse(data.get(rand).getPath());
                    //
                    // MediaControllerCompat.getMediaController(_activity)
                    //     .getTransportControls()
                    //     .playFromUri(path, null);
                }
            }
        });

        MediaControllerCompat mediaController =
                MediaControllerCompat.getMediaController(_activity);

        // Display the initial state
        // MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();
        if (pbState.getState() == PlaybackStateCompat.STATE_STOPPED) {
            TextView songTitle = _activity.findViewById(R.id.main_bottom_appbar_song_title);
            songTitle.setText(_activity.getString(R.string.default_current_song_title));
        }

        // Register a Callback to stay in sync
        mediaController.registerCallback(_controllerCallback);
    }

    /**
     * must be called before MediaBrowserCompat.connect()
     */
    public void setMediaBrowser(@NonNull MediaBrowserCompat mediaBrowser) {
        _mediaBrowser = mediaBrowser;
    }
}

