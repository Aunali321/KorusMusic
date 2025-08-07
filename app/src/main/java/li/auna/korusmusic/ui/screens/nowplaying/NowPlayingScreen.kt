package li.auna.korusmusic.ui.screens.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import li.auna.korusmusic.player.PlayerServiceConnection
import li.auna.korusmusic.player.RepeatMode
import li.auna.korusmusic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    onNavigateBack: () -> Unit,
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val playerManager by playerServiceConnection.playerManager.collectAsState()
    
    playerManager?.let { manager ->
        val playerState by manager.playerState.collectAsState()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Zinc950)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glass Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassSurface(shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Collapse",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                IconButton(onClick = { /* TODO: More options */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Glass Album Art Container
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .glassSurface(shape = RoundedCornerShape(24.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .glassSurfaceVariant(shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Song Info
            playerState.currentSong?.let { song ->
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary
                )
                
                Text(
                    text = song.artist.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Bar in Glass Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassSurface(shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                LinearProgressIndicator(
                    progress = if (playerState.duration > 0) {
                        (playerState.currentPosition.toFloat() / playerState.duration.toFloat())
                    } else 0f,
                    modifier = Modifier.fillMaxWidth(),
                    color = AccentBlue,
                    trackColor = GlassBorderLight
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(playerState.currentPosition),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatTime(playerState.duration),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Glass Control Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassSurface(shape = RoundedCornerShape(20.dp))
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = { 
                        playerServiceConnection.setShuffleMode(!playerState.shuffleMode)
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playerState.shuffleMode) {
                            AccentBlue
                        } else {
                            TextTertiary
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = { playerServiceConnection.seekToPrevious() },
                    modifier = Modifier
                        .size(56.dp)
                        .glassSurfaceVariant(shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(28.dp),
                        tint = TextPrimary
                    )
                }

                // Play/Pause - Main Control
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .glassSurface(shape = RoundedCornerShape(20.dp))
                        .background(
                            AccentBlue.copy(alpha = 0.2f),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { playerServiceConnection.togglePlayPause() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (playerState.isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(36.dp),
                            tint = AccentBlue
                        )
                    }
                }

                // Next
                IconButton(
                    onClick = { playerServiceConnection.seekToNext() },
                    modifier = Modifier
                        .size(56.dp)
                        .glassSurfaceVariant(shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(28.dp),
                        tint = TextPrimary
                    )
                }

                // Repeat
                IconButton(
                    onClick = { 
                        val nextMode = when (playerState.repeatMode) {
                            RepeatMode.OFF -> RepeatMode.ALL
                            RepeatMode.ALL -> RepeatMode.ONE
                            RepeatMode.ONE -> RepeatMode.OFF
                        }
                        playerServiceConnection.setRepeatMode(nextMode)
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = when (playerState.repeatMode) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (playerState.repeatMode != RepeatMode.OFF) {
                            AccentBlue
                        } else {
                            TextTertiary
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (playerState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = AccentBlue,
                    trackColor = GlassBorderLight
                )
            }
        }
    } ?: run {
        // No player manager available
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Zinc950),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .glassSurface(shape = RoundedCornerShape(20.dp))
                    .padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicOff,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = TextTertiary
                )
                Text(
                    text = "No music playing",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .glassSurface(shape = RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("Go Back", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}