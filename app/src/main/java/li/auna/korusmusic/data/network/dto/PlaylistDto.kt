package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    @SerialName("user_id") val userId: Long,
    val visibility: String = "private", // "public" or "private"
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    val duration: Int = 0,
    val owner: UserDto? = null,
    val songs: List<li.auna.korusmusic.data.network.dto.PlaylistSongDto> = emptyList(),
    @SerialName("song_count") val songCount: Int = 0
)


@Serializable
data class CreatePlaylistRequest(
    val name: String,
    val description: String? = null,
    val visibility: String = "private"
)

@Serializable
data class UpdatePlaylistRequest(
    val name: String,
    val description: String? = null,
    val visibility: String = "private"
)

@Serializable
data class AddSongsToPlaylistRequest(
    val songIds: List<Long>
)

@Serializable
data class RemoveSongsFromPlaylistRequest(
    val playlistSongIds: List<Long>
)

@Serializable
data class ReorderPlaylistRequest(
    val playlistSongId: Long,
    val newPosition: Int
)