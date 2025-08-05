package li.auna.korusmusic.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import androidx.room.withTransaction
import li.auna.korusmusic.data.database.KorusDatabase
import li.auna.korusmusic.data.mapper.toDomainModel
import li.auna.korusmusic.data.mapper.toEntity
import li.auna.korusmusic.data.network.KorusApiService
import li.auna.korusmusic.data.network.dto.*
import li.auna.korusmusic.domain.model.Playlist
import li.auna.korusmusic.domain.model.PlaylistSong
import li.auna.korusmusic.domain.model.User
import li.auna.korusmusic.domain.repository.PlaylistRepository
import li.auna.korusmusic.data.database.entities.PlaylistEntity

class PlaylistRepositoryImpl(
    private val apiService: KorusApiService,
    private val database: KorusDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        database.playlistDao().getAllPlaylists().map { entities ->
            entities.map { playlistEntity ->
                // For SSOT pattern, playlist songs will be loaded separately when needed
                // This keeps the Flow lightweight and follows the pattern
                playlistEntity.toDomainModel(null, emptyList())
            }
        }

    override fun getPlaylistsByUser(userId: Long): Flow<List<Playlist>> =
        database.playlistDao().getPlaylistsByUser(userId).map { entities ->
            entities.map { playlistEntity ->
                playlistEntity.toDomainModel(null, emptyList())
            }
        }

    override suspend fun getPlaylist(playlistId: Long): Playlist? =
        withContext(ioDispatcher) {
            val playlistEntity = database.playlistDao().getPlaylistById(playlistId) ?: return@withContext null
            
            // Get playlist songs with full details
            val playlistSongs = database.playlistDao().getPlaylistSongs(playlistId)
            val songs = playlistSongs.mapNotNull { playlistSongEntity ->
                val song = database.songDao().getSongById(playlistSongEntity.songId)
                val artist = song?.let { database.artistDao().getArtistById(it.artistId)?.toDomainModel() }
                val album = song?.let { database.albumDao().getAlbumById(it.albumId)?.toDomainModel(artist) }
                
                if (song != null && artist != null && album != null) {
                    PlaylistSong(
                        playlistSongId = playlistSongEntity.id,
                        position = playlistSongEntity.position,
                        song = song.toDomainModel(artist, album)
                    )
                } else null
            }
            
            playlistEntity.toDomainModel(null, songs)
        }

    override suspend fun searchPlaylists(query: String): List<Playlist> =
        withContext(ioDispatcher) {
            val playlistEntities = database.playlistDao().searchPlaylists(query)
            playlistEntities.map { playlistEntity ->
                playlistEntity.toDomainModel(null, emptyList())
            }
        }

    override suspend fun syncPlaylists() {
        withContext(ioDispatcher) {
            try {
                val playlists = apiService.getPlaylists()
                
                database.withTransaction {
                    // Sync playlists
                    database.playlistDao().insertPlaylists(playlists.map { it.toEntity() })
                    
                    // Sync playlist songs if included
                    playlists.forEach { playlistDto ->
                        if (playlistDto.songs.isNotEmpty()) {
                            // Extract unique songs and sync them
                            val songs = playlistDto.songs.map { it.song }
                            database.songDao().insertSongs(songs.map { it.toEntity() })
                            
                            // Sync playlist-song relationships using PlaylistDao
                            val playlistSongs = playlistDto.songs.map { playlistSongDto ->
                                playlistSongDto.toEntity(playlistDto.id)
                            }
                            database.playlistDao().insertPlaylistSongs(playlistSongs)
                        }
                    }
                }
            } catch (e: Exception) {
                // Log error and rethrow so UI can handle it properly
                throw e
            }
        }
    }

    override suspend fun createPlaylist(name: String, description: String?, isPublic: Boolean): Playlist {
        return withContext(ioDispatcher) {
            try {
                val request = CreatePlaylistRequest(
                    name = name,
                    description = description,
                    visibility = if (isPublic) "public" else "private"
                )
                val playlistDto = apiService.createPlaylist(request)
                
                // Sync the new playlist to local database
                database.playlistDao().insertPlaylist(playlistDto.toEntity())
                
                // Return the domain model
                playlistDto.toDomainModel()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun updatePlaylist(playlistId: Long, name: String, description: String?, isPublic: Boolean) {
        withContext(ioDispatcher) {
            try {
                val request = UpdatePlaylistRequest(
                    name = name,
                    description = description,
                    visibility = if (isPublic) "public" else "private"
                )
                val updatedPlaylistDto = apiService.updatePlaylist(playlistId, request)
                
                // Update local database
                database.playlistDao().insertPlaylist(updatedPlaylistDto.toEntity())
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        withContext(ioDispatcher) {
            try {
                apiService.deletePlaylist(playlistId)
                
                // Remove from local database
                database.withTransaction {
                    database.playlistDao().clearPlaylist(playlistId)
                    database.playlistDao().deletePlaylist(PlaylistEntity(playlistId, "", null, 0, false, "", "", 0, 0))
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        withContext(ioDispatcher) {
            try {
                val request = AddSongsToPlaylistRequest(songIds)
                apiService.addSongsToPlaylist(playlistId, request)
                
                // Refresh playlist from server to get updated song list with positions
                val updatedPlaylist = apiService.getPlaylist(playlistId)
                database.withTransaction {
                    // Update playlist
                    database.playlistDao().insertPlaylist(updatedPlaylist.toEntity())
                    
                    // Update playlist songs
                    database.playlistDao().clearPlaylist(playlistId)
                    val playlistSongs = updatedPlaylist.songs.map { it.toEntity(playlistId) }
                    database.playlistDao().insertPlaylistSongs(playlistSongs)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun removeSongsFromPlaylist(playlistId: Long, songIds: List<Long>) {
        withContext(ioDispatcher) {
            try {
                // First get playlist song IDs for the given song IDs
                val playlistSongs = database.playlistDao().getPlaylistSongs(playlistId)
                val playlistSongIds = playlistSongs.filter { songIds.contains(it.songId) }
                    .map { it.id } // Use the actual playlistSongId
                
                val request = RemoveSongsFromPlaylistRequest(playlistSongIds)
                apiService.removeSongsFromPlaylist(playlistId, request)
                
                // Remove from local database
                database.playlistDao().removePlaylistSongs(playlistId, songIds)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun reorderPlaylist(playlistId: Long, songId: Long, newPosition: Int) {
        withContext(ioDispatcher) {
            try {
                val request = ReorderPlaylistRequest(songId, newPosition)
                apiService.reorderPlaylist(playlistId, request)
                
                // Update local database position
                database.playlistDao().updateSongPosition(playlistId, songId, newPosition)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}