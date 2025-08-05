package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    @SerialName("user_id") val userId: Long,
    @SerialName("is_public") val isPublic: Boolean = false,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    val songs: List<PlaylistSongDto>? = null,
    @SerialName("song_count") val songCount: Int = 0,
    val duration: Int = 0
)

@Serializable
data class PlaylistSongDto(
    val id: Long,
    val title: String,
    @SerialName("album_id") val albumId: Long,
    @SerialName("artist_id") val artistId: Long,
    val duration: Int,
    val artist: ArtistDto,
    val album: AlbumDto,
    @SerialName("playlist_position") val playlistPosition: Int
)

@Serializable
data class CreatePlaylistRequest(
    val name: String,
    val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean = false
)

@Serializable
data class UpdatePlaylistRequest(
    val name: String,
    val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean = false
)

@Serializable
data class AddSongsToPlaylistRequest(
    @SerialName("song_ids") val songIds: List<Long>
)

@Serializable
data class RemoveSongsFromPlaylistRequest(
    @SerialName("song_ids") val songIds: List<Long>
)

@Serializable
data class ReorderPlaylistRequest(
    @SerialName("song_id") val songId: Long,
    @SerialName("new_position") val newPosition: Int
)