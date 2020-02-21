package com.muiz6.musicplayer.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.models.SongData;
import com.muiz6.musicplayer.repositories.Repository;

import java.util.ArrayList;

public class SongDataViewModel extends ViewModel {
    private MutableLiveData<ArrayList<SongData>> songList;

    public  SongDataViewModel(Context context) {
        this.songList = (MutableLiveData<ArrayList<SongData>>) Repository.getInstance().getSongList(context);
    }

    public LiveData<ArrayList<SongData>> getSongList() {
        return this.songList;
    }
}
