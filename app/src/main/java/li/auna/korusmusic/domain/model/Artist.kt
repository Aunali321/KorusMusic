package li.auna.korusmusic.domain.model

data class Artist(
    val id: Long,
    val name: String,
    val sortName: String? = null,
    val musicbrainzId: String? = null,
    val albumCount: Int = 0,
    val songCount: Int = 0,
    val isFollowed: Boolean = false
)