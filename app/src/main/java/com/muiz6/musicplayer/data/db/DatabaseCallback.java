package com.muiz6.musicplayer.data.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

public class DatabaseCallback extends RoomDatabase.Callback implements Runnable {

	private final ContentResolver _contentResolver;
	private final Context _context;
	private AudioDao _dao;

	@Inject
	public DatabaseCallback(@Named("Application") Context context) {
		_context = context;
		_contentResolver = context.getContentResolver();
	}

	@Override
	public void onOpen(@NonNull SupportSQLiteDatabase db) {
		super.onOpen(db);

		// todo: causing error for some reason
		// final String msg = _context.getString(R.string.scanning_music_library);
		// Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();

		new Thread(this).start();
	}

	@Override
	public void run() {
		_dao.deleteAll();

		// these constants do work on api-21
		final String[] projection = {MediaStore.Audio.AudioColumns.DATA,
				MediaStore.Audio.AudioColumns.DISPLAY_NAME,
				MediaStore.Audio.AudioColumns.TITLE,
				MediaStore.Audio.AudioColumns.ALBUM,
				MediaStore.Audio.AudioColumns.ARTIST,
				MediaStore.Audio.AudioColumns.DURATION};

		// ignore 30s and shorter tracks
		final String selection = MediaStore.Audio.AudioColumns.DURATION + " > ?";
		final String[] selectionArgs = {String.valueOf(TimeUnit.MILLISECONDS.convert(30,
				TimeUnit.SECONDS))};

		// fetch external storage content
		// todo: add permission request here
		final Cursor cursor = _contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projection,
				selection,
				selectionArgs,
				null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				_addRow(cursor);
			}
			cursor.close();
		}
	}

	public void setAudioDao(AudioDao dao) {
		_dao = dao;
	}

	private void _addRow(final Cursor cursor) {
		final String path = cursor.getString(0);
		final String displayName = cursor.getString(1);
		final String title = cursor.getString(2);
		final String album = cursor.getString(3);
		String artist = cursor.getString(4);
		if (artist.equals("<unknown>")) {
			artist = null;
		}
		final int duration = cursor.getInt(5);

		// extract genre
		final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(_context, Uri.parse(path));
		final String genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
		retriever.getEmbeddedPicture();

		final AudioEntity audioEntity = new AudioEntity(path, displayName);
		audioEntity.setTitle(title);
		audioEntity.setAlbum(album);
		audioEntity.setArtist(artist);
		audioEntity.setDuration(duration);
		audioEntity.setGenre(genre);

		_dao.insert(audioEntity);
	}
}
