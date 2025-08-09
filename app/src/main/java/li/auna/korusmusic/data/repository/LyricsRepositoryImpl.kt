package li.auna.korusmusic.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import li.auna.korusmusic.data.database.KorusDatabase
import li.auna.korusmusic.data.mapper.toDomainModel
import li.auna.korusmusic.data.mapper.toEntity
import li.auna.korusmusic.data.network.KorusApiServiceProvider
import li.auna.korusmusic.domain.model.Lyrics
import li.auna.korusmusic.domain.repository.LyricsRepository

class LyricsRepositoryImpl(
    private val apiServiceProvider: KorusApiServiceProvider,
    private val database: KorusDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : LyricsRepository {

    override fun getLyricsBySongId(songId: Long): Flow<List<Lyrics>> =
        database.lyricsDao().getLyricsBySongId(songId).map { entities ->
            entities.map { it.toDomainModel() }
        }

    override suspend fun getLyricsBySongAndLanguage(songId: Long, language: String): Lyrics? =
        withContext(ioDispatcher) {
            database.lyricsDao().getLyricsBySongAndLanguage(songId, language)?.toDomainModel()
        }

    override suspend fun getSyncedLyricsBySongId(songId: Long): Lyrics? =
        withContext(ioDispatcher) {
            database.lyricsDao().getSyncedLyricsBySongId(songId)?.toDomainModel()
        }

    override suspend fun getAvailableLanguagesBySongId(songId: Long): List<String> =
        withContext(ioDispatcher) {
            database.lyricsDao().getAvailableLanguagesBySongId(songId)
        }

    override suspend fun getLyricsBySongIds(songIds: List<Long>): List<Lyrics> =
        withContext(ioDispatcher) {
            database.lyricsDao().getLyricsBySongIds(songIds).map { it.toDomainModel() }
        }

    override suspend fun syncLyricsForSong(songId: Long) {
        withContext(ioDispatcher) {
            try {
                // Fetch song data including lyrics from API
                val songs = apiServiceProvider.getApiService().getSongs(ids = songId.toString())
                val song = songs.firstOrNull() ?: return@withContext
                
                // Clear existing lyrics for this song
                database.lyricsDao().deleteLyricsBySongId(songId)
                
                // Insert new lyrics
                val lyricsEntities = song.lyrics.map { it.toEntity() }
                if (lyricsEntities.isNotEmpty()) {
                    database.lyricsDao().insertAllLyrics(lyricsEntities)
                }
            } catch (e: Exception) {
                // Log error but don't throw
            }
        }
    }
}