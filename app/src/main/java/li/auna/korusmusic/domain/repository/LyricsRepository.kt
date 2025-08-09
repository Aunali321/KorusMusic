package li.auna.korusmusic.domain.repository

import kotlinx.coroutines.flow.Flow
import li.auna.korusmusic.domain.model.Lyrics

interface LyricsRepository {
    
    fun getLyricsBySongId(songId: Long): Flow<List<Lyrics>>
    
    suspend fun getLyricsBySongAndLanguage(songId: Long, language: String): Lyrics?
    
    suspend fun getSyncedLyricsBySongId(songId: Long): Lyrics?
    
    suspend fun getAvailableLanguagesBySongId(songId: Long): List<String>
    
    suspend fun getLyricsBySongIds(songIds: List<Long>): List<Lyrics>
    
    suspend fun syncLyricsForSong(songId: Long)
}