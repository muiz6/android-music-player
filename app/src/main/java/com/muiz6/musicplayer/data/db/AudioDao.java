package com.muiz6.musicplayer.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AudioDao {

	@Insert
	void insert(AudioEntity audio);

	@Query("DELETE FROM audio_table;")
	void deleteAll();

	@Query("SELECT * FROM audio_table ORDER BY display_name ASC;")
	LiveData<List<AudioEntity>> getAllAudio();

	@Query("SELECT * FROM audio_table WHERE album='ABC' ORDER BY album ASC;")
	List<AudioEntity> getAlbumList();
}
