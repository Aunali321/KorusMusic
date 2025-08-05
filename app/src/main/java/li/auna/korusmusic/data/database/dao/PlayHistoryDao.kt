package li.auna.korusmusic.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.data.database.entities.PlayHistoryEntity

@Dao
interface PlayHistoryDao {
    @Query("SELECT * FROM play_history ORDER BY played_at DESC LIMIT :limit")
    fun getRecentPlayHistory(limit: Int = 100): Flow<List<PlayHistoryEntity>>

    @Query("SELECT * FROM play_history WHERE song_id = :songId ORDER BY played_at DESC")
    fun getPlayHistoryForSong(songId: Long): Flow<List<PlayHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayHistory(playHistory: PlayHistoryEntity)

    @Query("DELETE FROM play_history WHERE played_at < :beforeDate")
    suspend fun deleteOldPlayHistory(beforeDate: String)

    @Query("DELETE FROM play_history")
    suspend fun deleteAllPlayHistory()

    @Query("""
        SELECT COUNT(*) as total_plays,
               SUM(duration_played) as total_listening_time,
               COUNT(DISTINCT song_id) as unique_songs
        FROM play_history
    """)
    suspend fun getUserStats(): UserStatsResult?

    data class UserStatsResult(
        @ColumnInfo(name = "total_plays") val totalPlays: Int,
        @ColumnInfo(name = "total_listening_time") val totalListeningTime: Int,
        @ColumnInfo(name = "unique_songs") val uniqueSongs: Int
    )
}