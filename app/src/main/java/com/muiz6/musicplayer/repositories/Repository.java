package com.muiz6.musicplayer.repositories;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.muiz6.musicplayer.models.SongData;

import java.util.ArrayList;

// singleton pattern
public class Repository {
    private static Repository repo;

    private Repository() {}

    public static Repository getInstance() {
        if (repo == null) {
            repo = new Repository();
        }
        return repo;
    }

    public LiveData<ArrayList<SongData>> getSongList(Context context) {
        final MutableLiveData<ArrayList<SongData>> data = new MutableLiveData<>();
        final ArrayList<SongData> items= new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor cursor = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                SongData audioModel = new SongData();
                String path = cursor.getString(0);
                String name = cursor.getString(1);
//                String album = c.getString(2);
//                String artist = c.getString(3);

                audioModel.setTitle(name);
//                audioModel.setaAlbum(album);
//                audioModel.setaArtist(artist);
                audioModel.setPath(path);

                items.add(audioModel);
            }
            cursor.close();
        }

        data.setValue(items);
        return data;
    }
}
