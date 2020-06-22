package com.muiz6.musicplayer.musicservice;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import com.muiz6.musicplayer.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

class _AsyncFetchAllSongs extends AsyncTask<Void, Void, String> {

    private WeakReference<MusicService> _service;

    public _AsyncFetchAllSongs(MusicService service) {
        _service = new WeakReference<>(service);
    }

    @Override
    protected String doInBackground(Void... voids) {
        MusicService service = _service.get();
        if (service == null) return null;

        MediaDescriptionCompat.Builder itemDescriptionBuilder = service.getItemDescriptionBuilder();
        ArrayList<MediaBrowserCompat.MediaItem> items = service.getMediaItems();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor cursor = service.getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

        if (cursor != null) {
            int i = 0;
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String name = cursor.getString(1);
//                String album = c.getString(2);
                String artist = cursor.getString(3);

                itemDescriptionBuilder.setTitle(name);
//                audioModel.setaAlbum(album);
                if (artist.equals("<unknown>")) {

                    // subtitle is artist name for media item
                    itemDescriptionBuilder.setSubtitle("Unknown Artist");
                }
                else {
                    itemDescriptionBuilder.setSubtitle(artist);
                }
                itemDescriptionBuilder.setMediaUri(Uri.parse(path));
                itemDescriptionBuilder.setMediaId(Constants.MEDIA_PREFIX_ALL_SONGS_ITEM + i++);

                items.add(new MediaBrowserCompat.MediaItem(itemDescriptionBuilder.build(),
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }
            cursor.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        MusicService service = _service.get();
        if (service == null) return;

        service.notifyChildrenChanged(Constants.MEDIA_ID_ALL_SONGS);
    }
}
