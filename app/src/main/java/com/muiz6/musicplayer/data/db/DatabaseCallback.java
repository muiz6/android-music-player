package com.muiz6.musicplayer.data.db;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import javax.inject.Inject;

public class DatabaseCallback extends RoomDatabase.Callback {

	private AudioDatabase _database;

	@Inject
	public DatabaseCallback() {}

	@Override
	public void onCreate(@NonNull SupportSQLiteDatabase db) {
		super.onCreate(db);
		_database.scanMusicLibrary();
	}

	public void setAudioDatabase(AudioDatabase database) {
		_database = database;
	}
}
