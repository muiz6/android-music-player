package com.muiz6.musicplayer.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.MainActivity;
import com.muiz6.musicplayer.ui.adapters.SongListRecyclerAdapter;
import com.muiz6.musicplayer.util.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SongFragment extends Fragment implements SongListRecyclerAdapter.OnItemClickListener {

    private static final String _TAG = "SongFragment";
    private RecyclerView _recyclerView;
    private SongListRecyclerAdapter _songListRecyclerAdapter;
    private ArrayList<MediaBrowserCompat.MediaItem> _songList;

    // required public ctor
    public SongFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        _songList = new ArrayList<>();

        // initializing adapter with empty songlist
        _songListRecyclerAdapter =
                new SongListRecyclerAdapter(new ArrayList<MediaBrowserCompat.MediaItem>(),
                        this);

        _recyclerView = (RecyclerView) LayoutInflater.from(context)
            .inflate(R.layout.fragment_list, null);

        _recyclerView.setAdapter(_songListRecyclerAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
             @Nullable ViewGroup container,
             @Nullable Bundle savedInstanceState) {
        return _recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity activity = (MainActivity) getActivity();
        activity.getMediaBrowser().subscribe(Constants.MEDIA_ID_ALL_SONGS,
                new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(@NonNull String parentId,
                                         @NonNull List<MediaBrowserCompat.MediaItem> children) {
                super.onChildrenLoaded(parentId, children);

                _songList = (ArrayList<MediaBrowserCompat.MediaItem>) children;
                _songListRecyclerAdapter
                        .setSongList(_songList);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        ((MainActivity)getActivity()).getMediaBrowser().unsubscribe(Constants.MEDIA_ID_ALL_SONGS);
    }

    @Override
    public void onItemClick(int position) {
        Log.d(_TAG, "onItemClick: clicked at " + position);

        // TODO: play from mediaId instead

        // -1 to bring recyclerview items and media items in sync
        int toPlay = position - 1;
        MediaControllerCompat.getMediaController(getActivity()).getTransportControls()
                .playFromUri(_songList.get(toPlay).getDescription().getMediaUri(), null);
    }

    public SongListRecyclerAdapter getAdapter() {
        return _songListRecyclerAdapter;
    }

    public RecyclerView getRecyclerView() {
        return _recyclerView;
    }
}
