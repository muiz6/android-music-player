package com.muiz6.musicplayer.musicservice.musicprovider;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import androidx.media.MediaBrowserServiceCompat;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * needs media browser service reference to notify children change
 */
class _AsyncFetchAllSongs extends AsyncTask<Void, Void, String> {

    private final List<MediaBrowserCompat.MediaItem> _itemList;
    private final WeakReference<MediaBrowserServiceCompat> _refService;

    public _AsyncFetchAllSongs(WeakReference<MediaBrowserServiceCompat> service,
            List<MediaBrowserCompat.MediaItem> itemList) {
        _refService = service;
        _itemList = itemList;
    }

    @Override
    protected String doInBackground(Void... voids) {
        MediaBrowserServiceCompat service = _refService.get();
        if (service != null) {
            MediaDescriptionCompat.Builder descriptionBuilder =
                    new MediaDescriptionCompat.Builder();

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.AudioColumns.DATA,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    MediaStore.Audio.ArtistColumns.ARTIST};
            Cursor cursor = service.getContentResolver().query(uri,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.AudioColumns.TITLE + " ASC");

            if (cursor != null) {
                int i = 0;
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    String name = cursor.getString(1);
                    String album = cursor.getString(2);
                    String artist = cursor.getString(3);

                    descriptionBuilder.setTitle(name);
                    if (artist.equals("<unknown>")) {

                        // subtitle is artist name for media item
                        descriptionBuilder.setSubtitle("Unknown Artist");
                    }
                    else {
                        descriptionBuilder.setSubtitle(artist);
                    }
                    descriptionBuilder.setDescription(album);
                    descriptionBuilder.setMediaUri(Uri.parse(path));
                    descriptionBuilder.setMediaId(MusicProvider.MEDIA_ID_ALL_SONGS + "." + i++);

                    _itemList.add(new MediaBrowserCompat.MediaItem(descriptionBuilder.build(),
                            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                }
                cursor.close();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        MediaBrowserServiceCompat service = _refService.get();
        if (service != null) {
            service.notifyChildrenChanged(MusicProvider.MEDIA_ID_ALL_SONGS);
        }
    }
}
