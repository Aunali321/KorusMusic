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
import li.auna.korusmusic.domain.model.Album
import li.auna.korusmusic.domain.repository.AlbumRepository

class AlbumRepositoryImpl(
    private val apiService: KorusApiService,
    private val database: KorusDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : AlbumRepository {

    override fun getAllAlbums(): Flow<List<Album>> =
        database.albumDao().getAllAlbums().map { entities ->
            entities.mapNotNull { albumEntity ->
                val artist = database.artistDao().getArtistById(albumEntity.artistId)?.toDomainModel()
                if (artist != null) {
                    albumEntity.toDomainModel(artist)
                } else null
            }
        }

    override fun getAlbumsByArtist(artistId: Long): Flow<List<Album>> =
        database.albumDao().getAlbumsByArtist(artistId).map { entities ->
            entities.mapNotNull { albumEntity ->
                val artist = database.artistDao().getArtistById(albumEntity.artistId)?.toDomainModel()
                if (artist != null) {
                    albumEntity.toDomainModel(artist)
                } else null
            }
        }

    override fun getLikedAlbums(): Flow<List<Album>> =
        database.albumDao().getLikedAlbums().map { entities ->
            entities.mapNotNull { albumEntity ->
                val artist = database.artistDao().getArtistById(albumEntity.artistId)?.toDomainModel()
                if (artist != null) {
                    albumEntity.toDomainModel(artist)
                } else null
            }
        }

    override fun getRecentlyAddedAlbums(limit: Int): Flow<List<Album>> =
        database.albumDao().getRecentlyAddedAlbums(limit).map { entities ->
            entities.mapNotNull { albumEntity ->
                val artist = database.artistDao().getArtistById(albumEntity.artistId)?.toDomainModel()
                if (artist != null) {
                    albumEntity.toDomainModel(artist)
                } else null
            }
        }

    override fun getAlbumsByYear(year: Int): Flow<List<Album>> =
        database.albumDao().getAlbumsByYear(year).map { entities ->
            entities.mapNotNull { albumEntity ->
                val artist = database.artistDao().getArtistById(albumEntity.artistId)?.toDomainModel()
                if (artist != null) {
                    albumEntity.toDomainModel(artist)
                } else null
            }
        }

    override suspend fun getAlbum(albumId: Long): Album? =
        withContext(ioDispatcher) {
            val albumEntity = database.albumDao().getAlbumById(albumId) ?: return@withContext null
            val artist = database.artistDao().getArtistById(albumEntity.artistId)?.toDomainModel()
            if (artist != null) {
                albumEntity.toDomainModel(artist)
            } else null
        }

    override suspend fun searchAlbums(query: String): List<Album> =
        withContext(ioDispatcher) {
            val albumEntities = database.albumDao().searchAlbums(query)
            albumEntities.mapNotNull { albumEntity ->
                val artist = database.artistDao().getArtistById(albumEntity.artistId)?.toDomainModel()
                if (artist != null) {
                    albumEntity.toDomainModel(artist)
                } else null
            }
        }

    override suspend fun syncAlbums() {
        withContext(ioDispatcher) {
            try {
                val albums = apiService.getAlbums(limit = 1000)
                
                database.withTransaction {
                    // First sync artists
                    database.artistDao().insertArtists(albums.mapNotNull { it.artist?.toEntity() })
                    
                    // Then sync albums
                    database.albumDao().insertAlbums(albums.map { it.toEntity() })
                    
                    // Finally sync songs if they're included
                    val allSongs = albums.flatMap { it.songs }
                    if (allSongs.isNotEmpty()) {
                        database.songDao().insertSongs(allSongs.map { it.toEntity() })
                    }
                }
            } catch (e: Exception) {
                // Log error and rethrow so UI can handle it properly
                throw e
            }
        }
    }

    override suspend fun likeAlbum(albumId: Long) {
        withContext(ioDispatcher) {
            try {
                apiService.likeAlbum(albumId)
                database.albumDao().updateLikedStatus(albumId, true)
            } catch (e: Exception) {
                // Handle error - could show toast or retry
                throw e
            }
        }
    }

    override suspend fun unlikeAlbum(albumId: Long) {
        withContext(ioDispatcher) {
            try {
                apiService.unlikeAlbum(albumId)
                database.albumDao().updateLikedStatus(albumId, false)
            } catch (e: Exception) {
                // Handle error - could show toast or retry
                throw e
            }
        }
    }
}