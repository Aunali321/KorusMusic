package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val songs: List<SongDto> = emptyList(),
    val albums: List<AlbumDto> = emptyList(),
    val artists: List<ArtistDto> = emptyList()
)