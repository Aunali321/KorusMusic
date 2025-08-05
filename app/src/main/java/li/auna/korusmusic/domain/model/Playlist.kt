package li.auna.korusmusic.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String? = null,
    val userId: Long,
    val visibility: String = "private", // "public" or "private"
    val createdAt: String,
    val updatedAt: String,
    val duration: Int = 0, // Total duration in seconds
    val owner: User? = null,
    val songs: List<PlaylistSong> = emptyList(),
    val songCount: Int = 0
) {
    fun getDurationFormatted(): String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        return if (hours > 0) {
            String.format("%d hr %d min", hours, minutes)
        } else {
            String.format("%d min", minutes)
        }
    }
}