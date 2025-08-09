package li.auna.korusmusic.domain.model

import kotlinx.serialization.Serializable

data class Lyrics(
    val id: Long,
    val songId: Long,
    val content: String,
    val type: LyricsType,
    val source: LyricsSource,
    val language: String, // ISO 639-2 language code
    val createdAt: String
) {
    fun isParseable(): Boolean = type == LyricsType.SYNCED && content.isNotBlank()
    
    fun getLanguageDisplayName(): String = when (language) {
        "eng" -> "English"
        "ara" -> "Arabic" 
        "urd" -> "Urdu"
        "hin" -> "Hindi"
        "spa" -> "Spanish"
        "fre" -> "French"
        "ger" -> "German"
        "jpn" -> "Japanese"
        "kor" -> "Korean"
        "chi" -> "Chinese"
        "por" -> "Portuguese"
        "ita" -> "Italian"
        "rus" -> "Russian"
        else -> language.uppercase()
    }
}

enum class LyricsType {
    SYNCED,    // Synchronized lyrics with timestamps
    UNSYNCED   // Plain text lyrics
}

enum class LyricsSource {
    EMBEDDED,     // From ID3 tags
    EXTERNAL_LRC, // From .lrc files
    EXTERNAL_TXT  // From .txt files
}

@Serializable
data class SynchronizedLyricsData(
    val metadata: LyricsMetadata,
    val lines: List<LyricsLine>
)

@Serializable
data class LyricsMetadata(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val by: String? = null,
    val offset: Int = 0, // Timing offset in milliseconds
    val length: String? = null, // Duration as string (mm:ss format)
    val language: String? = null
)

@Serializable
data class LyricsLine(
    val time: Long, // Timestamp in milliseconds
    val timeStr: String, // Original timestamp string [mm:ss.xx]
    val text: String
)