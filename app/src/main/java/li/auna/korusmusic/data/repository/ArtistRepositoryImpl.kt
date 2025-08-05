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
import li.auna.korusmusic.domain.model.Artist
import li.auna.korusmusic.domain.repository.ArtistRepository

class ArtistRepositoryImpl(
    private val apiService: KorusApiService,
    private val database: KorusDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : ArtistRepository {

    override fun getAllArtists(): Flow<List<Artist>> =
        database.artistDao().getAllArtists().map { entities ->
            entities.map { it.toDomainModel() }
        }

    override fun getFollowedArtists(): Flow<List<Artist>> =
        database.artistDao().getFollowedArtists().map { entities ->
            entities.map { it.toDomainModel() }
        }

    override suspend fun getArtist(artistId: Long): Artist? =
        withContext(ioDispatcher) {
            database.artistDao().getArtistById(artistId)?.toDomainModel()
        }

    override suspend fun searchArtists(query: String): List<Artist> =
        withContext(ioDispatcher) {
            database.artistDao().searchArtists(query).map { it.toDomainModel() }
        }

    override suspend fun syncArtists() {
        withContext(ioDispatcher) {
            try {
                val artists = apiService.getArtists(limit = 1000)
                
                database.withTransaction {
                    // Sync artists first
                    database.artistDao().insertArtists(artists.map { it.toEntity() })
                    
                    // Then sync their albums if included
                    val allAlbums = artists.flatMap { it.albums }
                    if (allAlbums.isNotEmpty()) {
                        database.albumDao().insertAlbums(allAlbums.map { it.toEntity() })
                    }
                    
                    // Finally sync top tracks if included
                    val allTopTracks = artists.flatMap { it.topTracks }
                    if (allTopTracks.isNotEmpty()) {
                        database.songDao().insertSongs(allTopTracks.map { it.toEntity() })
                    }
                }
            } catch (e: Exception) {
                // Log error and rethrow so UI can handle it properly
                throw e
            }
        }
    }

    override suspend fun followArtist(artistId: Long) {
        withContext(ioDispatcher) {
            try {
                apiService.followArtist(artistId)
                database.artistDao().updateFollowedStatus(artistId, true)
            } catch (e: Exception) {
                // Handle error - could show toast or retry
                throw e
            }
        }
    }

    override suspend fun unfollowArtist(artistId: Long) {
        withContext(ioDispatcher) {
            try {
                apiService.unfollowArtist(artistId)
                database.artistDao().updateFollowedStatus(artistId, false)
            } catch (e: Exception) {
                // Handle error - could show toast or retry
                throw e
            }
        }
    }
}