package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TopTrackDto(
    val id: Long,
    val title: String,
    val duration: Int,
    val album: AlbumDto? = null
)