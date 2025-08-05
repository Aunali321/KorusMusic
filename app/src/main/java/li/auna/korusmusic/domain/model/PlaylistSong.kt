package li.auna.korusmusic.domain.model

data class PlaylistSong(
    val playlistSongId: Long,
    val position: Int,
    val song: Song
)