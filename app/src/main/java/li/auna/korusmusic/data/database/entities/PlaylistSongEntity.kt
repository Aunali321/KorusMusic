package li.auna.korusmusic.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_songs",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["song_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["playlist_id"]),
        Index(value = ["song_id"]),
        Index(value = ["playlist_id", "position"]),
        Index(value = ["playlist_id", "song_id"], unique = true)
    ]
)
data class PlaylistSongEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "playlist_id") val playlistId: Long,
    @ColumnInfo(name = "song_id") val songId: Long,
    val position: Int
)