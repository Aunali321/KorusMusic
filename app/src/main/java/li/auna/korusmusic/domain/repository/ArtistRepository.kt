package li.auna.korusmusic.domain.repository

import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.domain.model.Artist

interface ArtistRepository {
    fun getAllArtists(): Flow<List<Artist>>
    fun getFollowedArtists(): Flow<List<Artist>>
    suspend fun getArtist(artistId: Long): Artist?
    suspend fun searchArtists(query: String): List<Artist>
    suspend fun syncArtists()
    suspend fun followArtist(artistId: Long)
    suspend fun unfollowArtist(artistId: Long)
}