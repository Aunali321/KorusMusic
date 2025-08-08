package li.auna.korusmusic.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import li.auna.korusmusic.player.PlayerServiceConnection
import li.auna.korusmusic.player.PlayerState
import li.auna.korusmusic.ui.theme.*

@Composable
fun MiniPlayer(
    playerServiceConnection: PlayerServiceConnection,
    onExpandToNowPlaying: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playerManager by playerServiceConnection.playerManager.collectAsState()
    
    playerManager?.let { manager ->
        val playerState by manager.playerState.collectAsState()
        
        AnimatedVisibility(
            visible = playerState.currentSong != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = modifier
        ) {
            MiniPlayerContent(
                playerState = playerState,
                onPlayPause = { playerServiceConnection.togglePlayPause() },
                onNext = { playerServiceConnection.seekToNext() },
                onPrevious = { playerServiceConnection.seekToPrevious() },
                onExpand = onExpandToNowPlaying
            )
        }
    }
}

@Composable
private fun MiniPlayerContent(
    playerState: PlayerState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onExpand() }
            .glassSurface(),
        colors = glassCardColors(),
        elevation = glassCardElevation()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Progress indicator at the bottom
            if (playerState.duration > 0) {
                val progress = if (playerState.duration > 0) {
                    (playerState.currentPosition.toFloat() / playerState.duration.toFloat()).coerceIn(0f, 1f)
                } else 0f
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.BottomCenter),
                    color = AccentBlue,
                    trackColor = GlassBorderLight.copy(alpha = 0.3f)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art
                playerState.currentSong?.let { song ->
                    CoverArtImage(
                        song = song,
                        size = 40.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                } ?: run {
                    // Fallback when no song is available
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .glassSurfaceVariant(shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Song Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    playerState.currentSong?.let { song ->
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    } ?: run {
                        Text(
                            text = "No song selected",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                            maxLines = 1
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Control Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // Play/Pause Button
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                AccentBlue.copy(alpha = 0.2f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = AccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // Next Button
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            // Loading indicator overlay
            if (playerState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Zinc950.copy(alpha = 0.7f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = AccentBlue,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}