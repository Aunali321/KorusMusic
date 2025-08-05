package li.auna.korusmusic.player

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentSong: li.auna.korusmusic.domain.model.Song? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackSpeed: Float = 1f,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleMode: Boolean = false,
    val queue: List<li.auna.korusmusic.domain.model.Song> = emptyList(),
    val currentIndex: Int = -1,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class RepeatMode {
    OFF,
    ONE,
    ALL
}