package com.muiz6.musicplayer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.adapters.SongListAdapter;
import com.muiz6.musicplayer.models.SongData;
import com.muiz6.musicplayer.viewmodels.SongDataViewModel;

import java.util.ArrayList;

public class SongsFragment extends Fragment {

//    private SongListAdapter songItemsAdapter;
    private ListView listView;
//    private final ArrayList<SongData> items;
    private SongDataViewModel songDataViewModel;
    private SongListAdapter songItemsAdapter;

    public SongsFragment() {
//        this.items = items;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // initialize empty
        songItemsAdapter = new SongListAdapter(context, R.layout.widget_song_item, new ArrayList<SongData>());

        // attaching fragment to view model
//        songDataViewModel = ViewModelProviders.of(this).get(SongDataViewModel.class);
        songDataViewModel = new SongDataViewModel(getActivity());
        songDataViewModel.getSongList().observe(this, new Observer<ArrayList<SongData>>() {
            @Override
            public void onChanged(ArrayList<SongData> songList) {
                songItemsAdapter.clear();
                songItemsAdapter.addAll(songList);
            }
        });

        this.listView = new ListView(context);
        listView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
        listView.setNestedScrollingEnabled(true);
        listView.setAdapter(songItemsAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        this.listView.setAdapter(this.songItemsAdapter);
        return this.listView;
    }
}
