package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: Long,
    val name: String,
    @SerialName("sort_name") val sortName: String? = null,
    @SerialName("musicbrainz_id") val musicbrainzId: String? = null,
    @SerialName("album_count") val albumCount: Int = 0,
    @SerialName("song_count") val songCount: Int = 0
)