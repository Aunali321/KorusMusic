package li.auna.korusmusic.data.util

import kotlinx.serialization.json.Json
import li.auna.korusmusic.domain.model.Lyrics
import li.auna.korusmusic.domain.model.LyricsType
import li.auna.korusmusic.domain.model.SynchronizedLyricsData
import li.auna.korusmusic.domain.model.LyricsLine

object LrcParser {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    fun parseSynchronizedLyrics(lyrics: Lyrics): SynchronizedLyricsData? {
        if (lyrics.type != LyricsType.SYNCED || lyrics.content.isBlank()) {
            return null
        }
        
        return try {
            json.decodeFromString<SynchronizedLyricsData>(lyrics.content)
        } catch (e: Exception) {
            // If JSON parsing fails, return null
            null
        }
    }
    
    fun getCurrentLyricsLine(
        synchronizedLyrics: SynchronizedLyricsData,
        currentPositionMs: Long
    ): Int? {
        val lines = synchronizedLyrics.lines
        if (lines.isEmpty()) return null
        
        // Account for offset - subtract offset from position to sync properly
        val adjustedPositionMs = currentPositionMs - synchronizedLyrics.metadata.offset
        
        // Find the current line by finding the latest line whose time has passed
        var currentLineIndex: Int? = null
        
        // Iterate through lines to find the current one
        for (i in lines.indices) {
            val line = lines[i]
            
            // If current position is past this line's time, it could be the current line
            if (adjustedPositionMs >= line.time) {
                currentLineIndex = i
            } else {
                // Once we find a line in the future, we stop
                break
            }
        }
        
        return currentLineIndex
    }
    
    fun getNextLyricsLine(
        synchronizedLyrics: SynchronizedLyricsData,
        currentLineIndex: Int
    ): LyricsLine? {
        val lines = synchronizedLyrics.lines
        val nextIndex = currentLineIndex + 1
        return if (nextIndex < lines.size) lines[nextIndex] else null
    }
    
    fun formatTimeString(timeMs: Long): String {
        val minutes = timeMs / 60000
        val seconds = (timeMs % 60000) / 1000
        val centiseconds = (timeMs % 1000) / 10
        return String.format("[%02d:%02d.%02d]", minutes, seconds, centiseconds)
    }
    
    fun parseTimeString(timeString: String): Long {
        // Parse LRC timestamp format [mm:ss.xx] to milliseconds
        val regex = """^\[(\d{1,2}):(\d{2})\.(\d{2})\]$""".toRegex()
        val matchResult = regex.find(timeString) ?: return 0L
        
        val minutes = matchResult.groupValues[1].toLongOrNull() ?: 0L
        val seconds = matchResult.groupValues[2].toLongOrNull() ?: 0L
        val centiseconds = matchResult.groupValues[3].toLongOrNull() ?: 0L
        
        return (minutes * 60 + seconds) * 1000 + centiseconds * 10
    }
    
    fun isValidSynchronizedLyrics(content: String): Boolean {
        return try {
            val data = json.decodeFromString<SynchronizedLyricsData>(content)
            data.lines.isNotEmpty() && data.lines.all { it.text.isNotBlank() }
        } catch (e: Exception) {
            false
        }
    }
    
    fun getPreferredLyrics(
        allLyrics: List<Lyrics>,
        preferredLanguage: String = "eng",
        preferSynced: Boolean = true
    ): Lyrics? {
        if (allLyrics.isEmpty()) return null
        
        // Filter by preferred language
        val preferredLanguageLyrics = allLyrics.filter { it.language == preferredLanguage }
        
        // If we have lyrics in preferred language, choose from them
        val candidateLyrics = if (preferredLanguageLyrics.isNotEmpty()) {
            preferredLanguageLyrics
        } else {
            allLyrics
        }
        
        // Prefer synced lyrics if requested
        return if (preferSynced) {
            candidateLyrics.find { it.type == LyricsType.SYNCED }
                ?: candidateLyrics.find { it.type == LyricsType.UNSYNCED }
        } else {
            candidateLyrics.find { it.type == LyricsType.UNSYNCED }
                ?: candidateLyrics.find { it.type == LyricsType.SYNCED }
        }
    }
}