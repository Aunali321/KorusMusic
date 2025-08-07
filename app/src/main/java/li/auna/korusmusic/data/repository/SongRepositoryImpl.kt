package li.auna.korusmusic.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import androidx.room.withTransaction
import li.auna.korusmusic.data.database.KorusDatabase
import li.auna.korusmusic.data.mapper.toDomainModel
import li.auna.korusmusic.data.mapper.toEntity
import li.auna.korusmusic.data.network.KorusApiServiceProvider
import li.auna.korusmusic.data.network.dto.AddSongsToPlaylistRequest
import li.auna.korusmusic.data.network.dto.RemoveSongsFromPlaylistRequest
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.SongRepository

class SongRepositoryImpl(
    private val apiServiceProvider: KorusApiServiceProvider,
    private val database: KorusDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> =
        database.songDao().getAllSongs().map { entities ->
            entities.map { songEntity ->
                val artist = database.artistDao().getArtistById(songEntity.artistId)?.toDomainModel()
                val album = database.albumDao().getAlbumById(songEntity.albumId)?.toDomainModel()
                if (artist != null && album != null) {
                    songEntity.toDomainModel(artist, album)
                } else null
            }.filterNotNull()
        }

    override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> =
        database.songDao().getSongsByAlbum(albumId).map { entities ->
            entities.map { songEntity ->
                val artist = database.artistDao().getArtistById(songEntity.artistId)?.toDomainModel()
                val album = database.albumDao().getAlbumById(songEntity.albumId)?.toDomainModel()
                if (artist != null && album != null) {
                    songEntity.toDomainModel(artist, album)
                } else null
            }.filterNotNull()
        }

    override fun getSongsByArtist(artistId: Long): Flow<List<Song>> =
        database.songDao().getSongsByArtist(artistId).map { entities ->
            entities.map { songEntity ->
                val artist = database.artistDao().getArtistById(songEntity.artistId)?.toDomainModel()
                val album = database.albumDao().getAlbumById(songEntity.albumId)?.toDomainModel()
                if (artist != null && album != null) {
                    songEntity.toDomainModel(artist, album)
                } else null
            }.filterNotNull()
        }

    override fun getLikedSongs(): Flow<List<Song>> =
        database.songDao().getLikedSongs().map { entities ->
            entities.map { songEntity ->
                val artist = database.artistDao().getArtistById(songEntity.artistId)?.toDomainModel()
                val album = database.albumDao().getAlbumById(songEntity.albumId)?.toDomainModel()
                if (artist != null && album != null) {
                    songEntity.toDomainModel(artist, album)
                } else null
            }.filterNotNull()
        }

    override fun getRecentlyPlayedSongs(limit: Int): Flow<List<Song>> =
        database.songDao().getRecentlyPlayedSongs(limit).map { entities ->
            entities.map { songEntity ->
                val artist = database.artistDao().getArtistById(songEntity.artistId)?.toDomainModel()
                val album = database.albumDao().getAlbumById(songEntity.albumId)?.toDomainModel()
                if (artist != null && album != null) {
                    songEntity.toDomainModel(artist, album)
                } else null
            }.filterNotNull()
        }

    override suspend fun getSong(songId: Long): Song? =
        withContext(ioDispatcher) {
            val songEntity = database.songDao().getSongById(songId) ?: return@withContext null
            val artist = database.artistDao().getArtistById(songEntity.artistId)?.toDomainModel()
            val album = database.albumDao().getAlbumById(songEntity.albumId)?.toDomainModel()
            if (artist != null && album != null) {
                songEntity.toDomainModel(artist, album)
            } else null
        }

    override suspend fun getSongs(songIds: List<Long>): List<Song> =
        withContext(ioDispatcher) {
            val songEntities = database.songDao().getSongsByIds(songIds)
            songEntities.mapNotNull { songEntity ->
                val artist = database.artistDao().getArtistById(songEntity.artistId)?.toDomainModel()
                val album = database.albumDao().getAlbumById(songEntity.albumId)?.toDomainModel()
                if (artist != null && album != null) {
                    songEntity.toDomainModel(artist, album)
                } else null
            }
        }

    override suspend fun searchSongs(query: String): List<Song> =
        withContext(ioDispatcher) {
            val songEntities = database.songDao().searchSongs(query)
            songEntities.mapNotNull { songEntity ->
                val artist = database.artistDao().getArtistById(songEntity.artistId)?.toDomainModel()
                val album = database.albumDao().getAlbumById(songEntity.albumId)?.toDomainModel()
                if (artist != null && album != null) {
                    songEntity.toDomainModel(artist, album)
                } else null
            }
        }

    override suspend fun syncSongs() {
        withContext(ioDispatcher) {
            try {
                // Get all songs directly from the songs endpoint
                val songs = apiServiceProvider.getApiService().getSongs(limit = 1000)
                
                database.withTransaction {
                    // Sync songs
                    database.songDao().insertSongs(songs.map { it.toEntity() })
                }
            } catch (e: Exception) {
                // Log error and rethrow so UI can handle it properly
                throw e
            }
        }
    }

    override suspend fun syncSongsByIds(songIds: List<Long>) {
        withContext(ioDispatcher) {
            try {
                val idsString = songIds.joinToString(",")
                val songs = apiServiceProvider.getApiService().getSongs(ids = idsString)
                database.songDao().insertSongs(songs.map { it.toEntity() })
            } catch (e: Exception) {
                // Log error but don't throw
            }
        }
    }

    override suspend fun likeSong(songId: Long) {
        withContext(ioDispatcher) {
            try {
                apiServiceProvider.getApiService().likeSongs(AddSongsToPlaylistRequest(listOf(songId)))
                database.songDao().updateLikedStatus(songId, true)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override suspend fun unlikeSong(songId: Long) {
        withContext(ioDispatcher) {
            try {
                apiServiceProvider.getApiService().unlikeSongs(RemoveSongsFromPlaylistRequest(listOf(songId)))
                database.songDao().updateLikedStatus(songId, false)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override suspend fun recordPlay(songId: Long, timestamp: String) {
        withContext(ioDispatcher) {
            database.songDao().incrementPlayCount(songId, timestamp)
        }
    }
}