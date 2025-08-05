package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: Long,
    val name: String,
    @SerialName("artist_id") val artistId: Long,
    @SerialName("album_artist_id") val albumArtistId: Long,
    val year: Int? = null,
    @SerialName("musicbrainz_id") val musicbrainzId: String? = null,
    @SerialName("cover_path") val coverPath: String? = null,
    @SerialName("date_added") val dateAdded: String,
    val artist: ArtistDto? = null,
    @SerialName("album_artist") val albumArtist: ArtistDto? = null,
    @SerialName("song_count") val songCount: Int = 0,
    val duration: Int = 0
)