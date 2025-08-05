package li.auna.korusmusic.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String? = null,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "is_public") val isPublic: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "song_count") val songCount: Int = 0,
    val duration: Int = 0 // Total duration in seconds
)