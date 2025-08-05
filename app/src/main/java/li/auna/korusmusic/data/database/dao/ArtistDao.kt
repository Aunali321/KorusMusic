package li.auna.korusmusic.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.data.database.entities.ArtistEntity

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artists ORDER BY name ASC")
    fun getAllArtists(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM artists WHERE id = :artistId")
    suspend fun getArtistById(artistId: Long): ArtistEntity?

    @Query("SELECT * FROM artists WHERE is_followed = 1 ORDER BY name ASC")
    fun getFollowedArtists(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM artists WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchArtists(query: String): List<ArtistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Update
    suspend fun updateArtist(artist: ArtistEntity)

    @Query("UPDATE artists SET is_followed = :isFollowed WHERE id = :artistId")
    suspend fun updateFollowedStatus(artistId: Long, isFollowed: Boolean)

    @Delete
    suspend fun deleteArtist(artist: ArtistEntity)

    @Query("DELETE FROM artists")
    suspend fun deleteAllArtists()
}