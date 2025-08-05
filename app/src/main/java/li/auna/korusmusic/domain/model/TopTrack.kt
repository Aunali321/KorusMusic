package li.auna.korusmusic.domain.model

data class TopTrack(
    val id: Long,
    val title: String,
    val duration: Int,
    val album: Album? = null
)