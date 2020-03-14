package com.muiz6.musicplayer.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.Repository;

import java.util.ArrayList;

public class SongDataViewModel extends ViewModel {

    private LiveData<ArrayList<SongDataModel>> songList;

    public  SongDataViewModel(Context context) {
        this.songList = Repository.getInstance(context).getSongList();
    }

    public LiveData<ArrayList<SongDataModel>> getSongList() {
        return this.songList;
    }
}
