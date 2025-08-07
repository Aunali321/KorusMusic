package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongDto(
    val id: Long,
    val title: String,
    @SerialName("album_id") val albumId: Long,
    @SerialName("artist_id") val artistId: Long,
    @SerialName("track_number") val trackNumber: Int? = null,
    @SerialName("disc_number") val discNumber: Int? = null,
    val duration: Int,
    @SerialName("file_path") val filePath: String,
    @SerialName("file_size") val fileSize: Long,
    @SerialName("file_modified") val fileModified: String,
    val bitrate: Int? = null,
    val format: String,
    @SerialName("date_added") val dateAdded: String,
    val artist: ArtistDto? = null
)