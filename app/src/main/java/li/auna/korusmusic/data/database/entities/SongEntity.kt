package li.auna.korusmusic.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["album_id"]),
        Index(value = ["artist_id"])
    ]
)
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    @ColumnInfo(name = "album_id") val albumId: Long,
    @ColumnInfo(name = "artist_id") val artistId: Long,
    @ColumnInfo(name = "track_number") val trackNumber: Int? = null,
    @ColumnInfo(name = "disc_number") val discNumber: Int? = null,
    val duration: Int, // Duration in seconds
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "file_size") val fileSize: Long,
    @ColumnInfo(name = "file_modified") val fileModified: String,
    val bitrate: Int? = null,
    val format: String,
    @ColumnInfo(name = "date_added") val dateAdded: String,
    @ColumnInfo(name = "is_liked") val isLiked: Boolean = false,
    @ColumnInfo(name = "play_count") val playCount: Int = 0,
    @ColumnInfo(name = "last_played") val lastPlayed: String? = null
)