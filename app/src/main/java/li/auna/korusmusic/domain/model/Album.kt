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
        if (baseUrl.isBlank() || coverPath == null) return null
        val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val normalizedPath = if (coverPath.startsWith("/")) coverPath else "/$coverPath"
        return "$normalizedBaseUrl$normalizedPath"
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