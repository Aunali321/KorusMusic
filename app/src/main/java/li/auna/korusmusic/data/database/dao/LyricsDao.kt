package li.auna.korusmusic.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.data.database.entities.LyricsEntity

@Dao
interface LyricsDao {
    
    @Query("SELECT * FROM lyrics WHERE song_id = :songId ORDER BY language ASC")
    fun getLyricsBySongId(songId: Long): Flow<List<LyricsEntity>>
    
    @Query("SELECT * FROM lyrics WHERE song_id = :songId ORDER BY language ASC")
    suspend fun getLyricsBySongIdSync(songId: Long): List<LyricsEntity>
    
    @Query("SELECT * FROM lyrics WHERE song_id = :songId AND language = :language LIMIT 1")
    suspend fun getLyricsBySongAndLanguage(songId: Long, language: String): LyricsEntity?
    
    @Query("SELECT * FROM lyrics WHERE song_id = :songId AND type = 'synced' LIMIT 1")
    suspend fun getSyncedLyricsBySongId(songId: Long): LyricsEntity?
    
    @Query("SELECT DISTINCT language FROM lyrics WHERE song_id = :songId ORDER BY language ASC")
    suspend fun getAvailableLanguagesBySongId(songId: Long): List<String>
    
    @Query("SELECT * FROM lyrics WHERE song_id IN (:songIds) ORDER BY song_id ASC, language ASC")
    suspend fun getLyricsBySongIds(songIds: List<Long>): List<LyricsEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyrics(lyrics: LyricsEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLyrics(lyrics: List<LyricsEntity>)
    
    @Delete
    suspend fun deleteLyrics(lyrics: LyricsEntity)
    
    @Query("DELETE FROM lyrics WHERE song_id = :songId")
    suspend fun deleteLyricsBySongId(songId: Long)
    
    @Query("DELETE FROM lyrics WHERE song_id = :songId AND language = :language")
    suspend fun deleteLyricsBySongAndLanguage(songId: Long, language: String)
    
    @Query("DELETE FROM lyrics")
    suspend fun deleteAllLyrics()
}