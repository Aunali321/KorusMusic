package li.auna.korusmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.LaunchedEffect
import android.graphics.drawable.BitmapDrawable
import li.auna.korusmusic.data.preferences.PreferencesManager
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.model.Album
import li.auna.korusmusic.ui.theme.TextTertiary
import li.auna.korusmusic.ui.theme.glassSurfaceVariant
import org.koin.androidx.compose.get

@Composable
fun CoverArtImage(
    song: Song,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    preferencesManager: PreferencesManager = get(),
    onBitmapLoaded: ((ImageBitmap?) -> Unit)? = null
) {
    val serverUrl by preferencesManager.serverUrl.collectAsState(initial = "")
    val coverUrl = song.getCoverUrl(serverUrl)
    
    if (coverUrl != null) {
        AsyncImage(
            model = coverUrl,
            contentDescription = "Album cover for ${song.title}",
            modifier = modifier
                .size(size)
                .clip(shape),
            contentScale = ContentScale.Crop,
            onState = { state ->
                when (state) {
                    is AsyncImagePainter.State.Success -> {
                        val drawable = state.result.drawable
                        if (drawable is BitmapDrawable) {
                            onBitmapLoaded?.invoke(drawable.bitmap.asImageBitmap())
                        } else {
                            onBitmapLoaded?.invoke(null)
                        }
                    }
                    else -> onBitmapLoaded?.invoke(null)
                }
            }
        )
    } else {
        // Fallback when no cover is available - use glass surface
        LaunchedEffect(song) {
            onBitmapLoaded?.invoke(null)
        }
        Box(
            modifier = modifier
                .size(size)
                .glassSurfaceVariant(shape = shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}

@Composable
fun AlbumCoverImage(
    album: Album,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    preferencesManager: PreferencesManager = get()
) {
    val serverUrl by preferencesManager.serverUrl.collectAsState(initial = "")
    val coverUrl = album.getCoverUrl(serverUrl)
    
    if (coverUrl != null) {
        AsyncImage(
            model = coverUrl,
            contentDescription = "Album cover for ${album.name}",
            modifier = modifier
                .size(size)
                .clip(shape),
            contentScale = ContentScale.Crop
        )
    } else {
        // Fallback when no cover is available - use glass surface
        Box(
            modifier = modifier
                .size(size)
                .glassSurfaceVariant(shape = shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}

@Composable
fun CoverArtImage(
    coverUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    if (coverUrl != null) {
        AsyncImage(
            model = coverUrl,
            contentDescription = contentDescription,
            modifier = modifier
                .size(size)
                .clip(shape),
            contentScale = ContentScale.Crop
        )
    } else {
        // Fallback when no cover is available - use glass surface
        Box(
            modifier = modifier
                .size(size)
                .glassSurfaceVariant(shape = shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}