package li.auna.korusmusic.domain.repository

import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.domain.model.Song

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
    fun getSongsByArtist(artistId: Long): Flow<List<Song>>
    fun getLikedSongs(): Flow<List<Song>>
    fun getRecentlyPlayedSongs(limit: Int = 50): Flow<List<Song>>
    suspend fun getSong(songId: Long): Song?
    suspend fun getSongs(songIds: List<Long>): List<Song>
    suspend fun searchSongs(query: String): List<Song>
    suspend fun syncSongs()
    suspend fun syncSongsByIds(songIds: List<Long>)
    suspend fun likeSong(songId: Long)
    suspend fun unlikeSong(songId: Long)
    suspend fun recordPlay(songId: Long, timestamp: String)
}