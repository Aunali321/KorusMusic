package li.auna.korusmusic.domain.repository

import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.domain.model.Album

interface AlbumRepository {
    fun getAllAlbums(): Flow<List<Album>>
    fun getAlbumsByArtist(artistId: Long): Flow<List<Album>>
    fun getLikedAlbums(): Flow<List<Album>>
    fun getRecentlyAddedAlbums(limit: Int = 20): Flow<List<Album>>
    fun getAlbumsByYear(year: Int): Flow<List<Album>>
    suspend fun getAlbum(albumId: Long): Album?
    suspend fun searchAlbums(query: String): List<Album>
    suspend fun syncAlbums()
    suspend fun likeAlbum(albumId: Long)
    suspend fun unlikeAlbum(albumId: Long)
}