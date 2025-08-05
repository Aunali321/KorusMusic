package li.auna.korusmusic.domain.model

data class Album(
    val id: Long,
    val name: String,
    val artistId: Long,
    val albumArtistId: Long,
    val year: Int? = null,
    val musicbrainzId: String? = null,
    val coverPath: String? = null,
    val dateAdded: String,
    val artist: Artist? = null,
    val albumArtist: Artist? = null,
    val songCount: Int = 0,
    val duration: Int = 0, // Total duration in seconds
    val isLiked: Boolean = false
) {
    fun getCoverUrl(baseUrl: String): String? {
        return if (coverPath != null) {
            "${baseUrl}albums/$id/cover"
        } else null
    }
    
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