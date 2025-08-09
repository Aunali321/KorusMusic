package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LyricsDto(
    val id: Long,
    @SerialName("song_id") val songId: Long,
    val content: String,
    val type: String, // "synced" or "unsynced"
    val source: String, // "embedded", "external_lrc", or "external_txt"
    val language: String, // ISO 639-2 language code
    @SerialName("created_at") val createdAt: String
)