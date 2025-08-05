package li.auna.korusmusic.domain.model

data class PlayHistory(
    val id: Long,
    val songId: Long,
    val playedAt: String,
    val durationPlayed: Int,
    val completed: Boolean,
    val song: Song
)