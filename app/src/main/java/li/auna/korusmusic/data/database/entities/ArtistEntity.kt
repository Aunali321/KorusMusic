package li.auna.korusmusic.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey val id: Long,
    val name: String,
    @ColumnInfo(name = "sort_name") val sortName: String? = null,
    @ColumnInfo(name = "musicbrainz_id") val musicbrainzId: String? = null,
    @ColumnInfo(name = "album_count") val albumCount: Int = 0,
    @ColumnInfo(name = "song_count") val songCount: Int = 0,
    @ColumnInfo(name = "is_followed") val isFollowed: Boolean = false
)