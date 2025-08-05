package li.auna.korusmusic.domain.repository

import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.domain.model.Playlist

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistsByUser(userId: Long): Flow<List<Playlist>>
    suspend fun getPlaylist(playlistId: Long): Playlist?
    suspend fun searchPlaylists(query: String): List<Playlist>
    suspend fun syncPlaylists()
    suspend fun createPlaylist(name: String, description: String?, isPublic: Boolean): Playlist
    suspend fun updatePlaylist(playlistId: Long, name: String, description: String?, isPublic: Boolean)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)
    suspend fun removeSongsFromPlaylist(playlistId: Long, songIds: List<Long>)
    suspend fun reorderPlaylist(playlistId: Long, songId: Long, newPosition: Int)
}