package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayHistoryDto(
    val id: Long,
    @SerialName("song_id") val songId: Long,
    @SerialName("played_at") val playedAt: String,
    @SerialName("duration_played") val durationPlayed: Int,
    val completed: Boolean,
    val song: SongDto
)

@Serializable
data class RecordPlayRequest(
    @SerialName("song_id") val songId: Long,
    @SerialName("played_at") val playedAt: String,
    @SerialName("duration_played") val durationPlayed: Int,
    val completed: Boolean
)

@Serializable
data class UserStatsDto(
    @SerialName("total_plays") val totalPlays: Int,
    @SerialName("total_listening_time") val totalListeningTime: Int,
    @SerialName("unique_songs") val uniqueSongs: Int,
    @SerialName("unique_artists") val uniqueArtists: Int,
    @SerialName("unique_albums") val uniqueAlbums: Int,
    @SerialName("top_artists") val topArtists: List<TopArtistDto> = emptyList(),
    @SerialName("top_albums") val topAlbums: List<TopAlbumDto> = emptyList(),
    @SerialName("top_songs") val topSongs: List<TopSongDto> = emptyList()
)

@Serializable
data class TopArtistDto(
    val id: Long,
    val name: String,
    @SerialName("play_count") val playCount: Int
)

@Serializable
data class TopAlbumDto(
    val id: Long,
    val name: String,
    @SerialName("artist_name") val artistName: String,
    @SerialName("play_count") val playCount: Int
)

@Serializable
data class TopSongDto(
    val id: Long,
    val title: String,
    @SerialName("artist_name") val artistName: String,
    @SerialName("album_name") val albumName: String,
    @SerialName("play_count") val playCount: Int
)