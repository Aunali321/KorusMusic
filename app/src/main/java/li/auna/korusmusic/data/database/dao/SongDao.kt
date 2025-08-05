package li.auna.korusmusic.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.data.database.entities.SongEntity

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long): SongEntity?

    @Query("SELECT * FROM songs WHERE id IN (:songIds)")
    suspend fun getSongsByIds(songIds: List<Long>): List<SongEntity>

    @Query("SELECT * FROM songs WHERE album_id = :albumId ORDER BY track_number ASC, title ASC")
    fun getSongsByAlbum(albumId: Long): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE artist_id = :artistId ORDER BY title ASC")
    fun getSongsByArtist(artistId: Long): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE is_liked = 1 ORDER BY title ASC")
    fun getLikedSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY last_played DESC LIMIT :limit")
    fun getRecentlyPlayedSongs(limit: Int = 50): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    suspend fun searchSongs(query: String): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("UPDATE songs SET is_liked = :isLiked WHERE id = :songId")
    suspend fun updateLikedStatus(songId: Long, isLiked: Boolean)

    @Query("UPDATE songs SET play_count = play_count + 1, last_played = :timestamp WHERE id = :songId")
    suspend fun incrementPlayCount(songId: Long, timestamp: String)

    @Delete
    suspend fun deleteSong(song: SongEntity)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    @Query("DELETE FROM songs WHERE id IN (:songIds)")
    suspend fun deleteSongs(songIds: List<Long>)
}