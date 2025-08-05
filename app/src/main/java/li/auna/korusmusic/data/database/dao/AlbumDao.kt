package li.auna.korusmusic.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.data.database.entities.AlbumEntity

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums ORDER BY name ASC")
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumById(albumId: Long): AlbumEntity?

    @Query("SELECT * FROM albums WHERE artist_id = :artistId ORDER BY year DESC, name ASC")
    fun getAlbumsByArtist(artistId: Long): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE is_liked = 1 ORDER BY name ASC")
    fun getLikedAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums ORDER BY date_added DESC LIMIT :limit")
    fun getRecentlyAddedAlbums(limit: Int = 20): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE year = :year ORDER BY name ASC")
    fun getAlbumsByYear(year: Int): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchAlbums(query: String): List<AlbumEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Update
    suspend fun updateAlbum(album: AlbumEntity)

    @Query("UPDATE albums SET is_liked = :isLiked WHERE id = :albumId")
    suspend fun updateLikedStatus(albumId: Long, isLiked: Boolean)

    @Delete
    suspend fun deleteAlbum(album: AlbumEntity)

    @Query("DELETE FROM albums")
    suspend fun deleteAllAlbums()
}