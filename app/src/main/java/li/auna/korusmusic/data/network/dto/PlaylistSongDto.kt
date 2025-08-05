package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistSongDto(
    val playlistSongId: Long,
    val position: Int,
    val song: SongDto
)