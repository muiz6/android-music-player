package com.muiz6.musicplayer.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.ui.adapters.SongListAdapter;
import com.muiz6.musicplayer.viewmodels.SongDataViewModel;

import java.util.ArrayList;

public class SongFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SongListAdapter mSongListAdapter;

    public SongFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // initializing empty songlist
        mSongListAdapter = new SongListAdapter();

        // attaching fragment to view model
        final SongDataViewModel mSongDataViewModel = new ViewModelProvider(this).get(SongDataViewModel.class);
        mSongDataViewModel.getSongList().observe(this, new Observer<ArrayList<SongDataModel>>() {
            @Override
            public void onChanged(ArrayList<SongDataModel> songList) {

            // songList will be null until ready
            if (songList != null) {
                mSongListAdapter.setSongList(songList);
            }
            }
        });

        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setAdapter(mSongListAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRecyclerView;
    }
}
