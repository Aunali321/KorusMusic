package li.auna.korusmusic.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "albums",
    foreignKeys = [
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["album_artist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["artist_id"]),
        Index(value = ["album_artist_id"])
    ]
)
data class AlbumEntity(
    @PrimaryKey val id: Long,
    val name: String,
    @ColumnInfo(name = "artist_id") val artistId: Long,
    @ColumnInfo(name = "album_artist_id") val albumArtistId: Long,
    val year: Int? = null,
    @ColumnInfo(name = "musicbrainz_id") val musicbrainzId: String? = null,
    @ColumnInfo(name = "cover_path") val coverPath: String? = null,
    @ColumnInfo(name = "date_added") val dateAdded: String,
    @ColumnInfo(name = "song_count") val songCount: Int = 0,
    val duration: Int = 0, // Total duration in seconds
    @ColumnInfo(name = "is_liked") val isLiked: Boolean = false
)