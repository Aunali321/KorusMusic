package li.auna.korusmusic.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.data.database.entities.PlaylistEntity
import li.auna.korusmusic.data.database.entities.PlaylistSongEntity

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE user_id = :userId ORDER BY name ASC")
    fun getPlaylistsByUser(userId: Long): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchPlaylists(query: String): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists")
    suspend fun deleteAllPlaylists()

    // Playlist songs operations
    @Query("SELECT * FROM playlist_songs WHERE playlist_id = :playlistId ORDER BY position ASC")
    suspend fun getPlaylistSongs(playlistId: Long): List<PlaylistSongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(playlistSong: PlaylistSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongs(playlistSongs: List<PlaylistSongEntity>)

    @Query("DELETE FROM playlist_songs WHERE playlist_id = :playlistId AND song_id = :songId")
    suspend fun removePlaylistSong(playlistId: Long, songId: Long)

    @Query("DELETE FROM playlist_songs WHERE playlist_id = :playlistId AND song_id IN (:songIds)")
    suspend fun removePlaylistSongs(playlistId: Long, songIds: List<Long>)

    @Query("DELETE FROM playlist_songs WHERE playlist_id = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Query("UPDATE playlist_songs SET position = :newPosition WHERE playlist_id = :playlistId AND song_id = :songId")
    suspend fun updateSongPosition(playlistId: Long, songId: Long, newPosition: Int)

    @Transaction
    suspend fun reorderPlaylistSongs(playlistId: Long, songOrders: List<Pair<Long, Int>>) {
        songOrders.forEach { (songId, position) ->
            updateSongPosition(playlistId, songId, position)
        }
    }
}