package li.auna.korusmusic.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lyrics",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["song_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["song_id"]),
        Index(value = ["song_id", "language", "type"], unique = true)
    ]
)
data class LyricsEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "song_id") val songId: Long,
    val content: String,
    val type: String, // "synced" or "unsynced"
    val source: String, // "embedded", "external_lrc", or "external_txt"
    val language: String, // ISO 639-2 language code
    @ColumnInfo(name = "created_at") val createdAt: String
)