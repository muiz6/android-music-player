package com.muiz6.musicplayer.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.muiz6.musicplayer.data.db.pojos.AlbumPojo;
import com.muiz6.musicplayer.data.db.pojos.ArtistPojo;
import com.muiz6.musicplayer.data.db.pojos.GenrePojo;
import com.muiz6.musicplayer.data.db.pojos.SongPojo;

import java.util.List;

@Dao
public interface AudioDao {

	@Insert
	void insert(AudioEntity audio);

	@Query("DELETE FROM audio_table;")
	void deleteAll();

	@Query("SELECT path, display_name, title, artist, duration, rowid FROM audio_table ORDER BY display_name ASC;")
	List<SongPojo> getAllSongList();

	// group by artist and not album because different artist may share same album name
	@Query("SELECT album, artist, album_id FROM audio_table GROUP BY album_id;")
	List<AlbumPojo> getAllAlbumList();

	@Query("Select artist, artist_id FROM audio_table WHERE artist IS NOT NULL GROUP BY artist_id;")
	List<ArtistPojo> getAllArtistList();

	@Query("Select genre, genre_id FROM audio_table WHERE genre IS NOT NULL GROUP BY genre;")
	List<GenrePojo> getAllGenreList();

	@Query("SELECT path, display_name, title, artist, duration, rowid FROM audio_table WHERE artist_id=:id ORDER BY display_name ASC;")
	List<SongPojo> getSongListByArtistId(int id);

	@Query("SELECT path, display_name, title, artist, duration, rowid FROM audio_table WHERE genre_id=:id ORDER BY display_name ASC;")
	List<SongPojo> getSongListByGenreId(int id);

	@Query("SELECT path, display_name, title, artist, duration, rowid FROM audio_table WHERE album_id=:id ORDER BY display_name ASC;")
	List<SongPojo> getSongListByAlbumId(int id);

	@Query("SELECT album, artist, album_id FROM audio_table WhERE artist_id=:id GROUP BY artist;")
	List<AlbumPojo> getAlbumListByArtistId(int id);

	@Query("SELECT album, artist, album_id FROM audio_table WhERE genre_id=:id GROUP BY artist;")
	List<AlbumPojo> getAlbumListByGenreId(int id);
}
