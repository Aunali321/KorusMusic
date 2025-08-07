package li.auna.korusmusic.domain.model

data class Song(
    val id: Long,
    val title: String,
    val albumId: Long,
    val artistId: Long,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val duration: Int, // Duration in seconds
    val filePath: String,
    val fileSize: Long,
    val fileModified: String,
    val bitrate: Int? = null,
    val format: String,
    val dateAdded: String,
    val artist: Artist,
    val album: Album,
    val isLiked: Boolean = false,
    val playCount: Int = 0,
    val lastPlayed: String? = null
) {
    fun getStreamUrl(baseUrl: String): String {
        return "${baseUrl}api/songs/$id/stream"
    }
    
    fun getDurationFormatted(): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}