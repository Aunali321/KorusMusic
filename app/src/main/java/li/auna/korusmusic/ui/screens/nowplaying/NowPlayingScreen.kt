package li.auna.korusmusic.ui.screens.nowplaying

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.ImageBitmap
import li.auna.korusmusic.player.PlayerServiceConnection
import li.auna.korusmusic.player.RepeatMode
import li.auna.korusmusic.ui.components.CoverArtImage
import li.auna.korusmusic.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    onNavigateBack: () -> Unit,
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val playerManager by playerServiceConnection.playerManager.collectAsState()

    playerManager?.let { manager ->
        val playerState by manager.playerState.collectAsState()

        // State to hold the cover art bitmap for dynamic color extraction
        var coverArtBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        
        // Extract dynamic colors from album art
        val dynamicColorScheme = rememberDynamicColorScheme(coverArtBitmap)

        // Animated background color
        val animatedBackgroundColor by animateColorAsState(
            targetValue = dynamicColorScheme.backgroundColor,
            label = "background_color"
        )

        // Bottom sheet state - Start fully collapsed (peek only)
        val bottomSheetPeekHeight = 88.dp
        var bottomSheetTargetOffset by remember {
            mutableFloatStateOf(1f) // 0.0 = expanded, 1.0 = collapsed
        }
        var isDragging by remember { mutableStateOf(false) }

        val bottomSheetOffset by animateFloatAsState(
            targetValue = bottomSheetTargetOffset,
            animationSpec = if (isDragging) spring(stiffness = 2000f) else spring(),
            label = "bottom_sheet_offset"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBackgroundColor)
        ) {
            // Main Content - Album art + controls
            MainContent(
                playerState = playerState,
                colorScheme = dynamicColorScheme,
                playerServiceConnection = playerServiceConnection,
                onNavigateBack = onNavigateBack,
                bottomPeekPadding = bottomSheetPeekHeight,
                onCoverArtLoaded = { bitmap -> coverArtBitmap = bitmap },
                modifier = Modifier.fillMaxSize()
            )

            // Draggable Bottom Sheet
            DraggableBottomSheet(
                playerState = playerState,
                colorScheme = dynamicColorScheme,
                offset = bottomSheetOffset,
                onOffsetChange = { bottomSheetTargetOffset = it },
                onDragStateChange = { isDragging = it },
                onTabClick = { tabIndex ->
                    if (tabIndex == 0) { // UP NEXT tab
                        bottomSheetTargetOffset = 0f // Expand
                    }
                },
                peekHeight = bottomSheetPeekHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
            )
        }
    } ?: run {
        // No player manager available - Fallback UI
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
                    modifier = Modifier.padding(top = 20.dp),
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

@Composable
private fun MainContent(
    playerState: li.auna.korusmusic.player.PlayerState,
    colorScheme: DynamicColorScheme,
    playerServiceConnection: PlayerServiceConnection,
    onNavigateBack: () -> Unit,
    bottomPeekPadding: Dp,
    onCoverArtLoaded: (ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp + bottomPeekPadding * 0.25f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar - Clean design without glass surface
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Back",
                    tint = colorScheme.onSurfaceColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurfaceColor
            )

            IconButton(onClick = { /* TODO: More options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = colorScheme.onSurfaceColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Album Art - Smaller so everything fits on one screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .heightIn(max = 240.dp)
                .dynamicGlassSurface(
                    colorScheme = colorScheme,
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            playerState.currentSong?.let { song ->
                CoverArtImage(
                    song = song,
                    size = 200.dp,
                    shape = RoundedCornerShape(24.dp),
                    onBitmapLoaded = onCoverArtLoaded
                )
            } ?: run {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = colorScheme.onSurfaceVariantColor
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Song Info
        playerState.currentSong?.let { song ->
            Text(
                text = song.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = colorScheme.onSurfaceColor
            )

            Text(
                text = song.artist.name,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurfaceVariantColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .dynamicGlassSurface(
                    colorScheme = colorScheme,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            LinearProgressIndicator(
                progress = if (playerState.duration > 0) {
                    (playerState.currentPosition.toFloat() / playerState.duration.toFloat())
                } else 0f,
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.primaryColor,
                trackColor = colorScheme.outlineVariantColor
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
                    color = colorScheme.onSurfaceVariantColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatTime(playerState.duration),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariantColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Control Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .dynamicGlassSurface(
                    colorScheme = colorScheme,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shuffle
            IconButton(
                onClick = {
                    playerServiceConnection.setShuffleMode(!playerState.shuffleMode)
                },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (playerState.shuffleMode) {
                        colorScheme.primaryColor
                    } else {
                        colorScheme.onSurfaceVariantColor
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            // Previous
            IconButton(
                onClick = { playerServiceConnection.seekToPrevious() },
                modifier = Modifier
                    .size(52.dp)
                    .dynamicGlassSurfaceVariant(
                        colorScheme = colorScheme,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(26.dp),
                    tint = colorScheme.onSurfaceColor
                )
            }

            // Play/Pause - Main Control
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        colorScheme.primaryColor.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
                    .dynamicGlassSurface(
                        colorScheme = colorScheme,
                        shape = RoundedCornerShape(20.dp)
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
                        modifier = Modifier.size(32.dp),
                        tint = colorScheme.primaryColor
                    )
                }
            }

            // Next
            IconButton(
                onClick = { playerServiceConnection.seekToNext() },
                modifier = Modifier
                    .size(52.dp)
                    .dynamicGlassSurfaceVariant(
                        colorScheme = colorScheme,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(26.dp),
                    tint = colorScheme.onSurfaceColor
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
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = when (playerState.repeatMode) {
                        RepeatMode.ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (playerState.repeatMode != RepeatMode.OFF) {
                        colorScheme.primaryColor
                    } else {
                        colorScheme.onSurfaceVariantColor
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DraggableBottomSheet(
    playerState: li.auna.korusmusic.player.PlayerState,
    colorScheme: DynamicColorScheme,
    offset: Float,
    onOffsetChange: (Float) -> Unit,
    onDragStateChange: (Boolean) -> Unit,
    onTabClick: (Int) -> Unit = {},
    peekHeight: Dp,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    // Full-height sheet; no dynamic measurement needed
    var headerHeightPx by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = modifier
    ) {
        val constraintsMaxHeight = maxHeight
        val parentHeightPx = with(density) { maxHeight.toPx() }
        val peekHeightPxDefault = with(density) { peekHeight.toPx() }
        val peekHeightPx = if (headerHeightPx > 0f) headerHeightPx else peekHeightPxDefault
        val expandedTopPx = 0f
        val collapsedTopPx = (parentHeightPx - peekHeightPx).coerceAtLeast(0f)
        val topYPx = expandedTopPx + (collapsedTopPx - expandedTopPx) * offset

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight)
                .offset { IntOffset(0, topYPx.roundToInt()) }
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .dynamicGlassSurface(
                    colorScheme = colorScheme,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .pointerInput(collapsedTopPx, expandedTopPx) {
                    var startDragOffset = 0f
                    var accumulatedDrag = 0f
                    var lastDragAmount = 0f
                    var currentDragOffset = 0f
                    detectVerticalDragGestures(
                        onDragStart = {
                            onDragStateChange(true)
                            startDragOffset = offset
                            accumulatedDrag = 0f
                            currentDragOffset = offset
                            lastDragAmount = 0f
                        },
                        onVerticalDrag = { _, dragAmount ->
                            lastDragAmount = dragAmount
                            accumulatedDrag += dragAmount
                            val range = (collapsedTopPx - expandedTopPx).coerceAtLeast(1f)
                            val offsetDelta = accumulatedDrag / range
                            currentDragOffset = (startDragOffset + offsetDelta).coerceIn(0f, 1f)
                            onOffsetChange(currentDragOffset)
                        },
                        onDragEnd = {
                            onDragStateChange(false)
                           // Use the calculated drag position, not the animated offset
                            val targetOffset = when {
                                // Fast swipe up - expand
                                lastDragAmount < -20f -> 0f
                                // Fast swipe down - collapse
                                lastDragAmount > 20f -> 1f
                                // Position-based - if more than halfway up, expand
                                currentDragOffset < 0.5f -> 0f
                                // Otherwise collapse
                                else -> 1f
                            }

                            onOffsetChange(targetOffset)
                        }
                    )
                }
        ) {
            // Header (handle + tabs) measured to define the peek height exactly
            Column(
                modifier = Modifier
                    .onGloballyPositioned { headerHeightPx = it.size.height.toFloat() }
            ) {
                // Drag Handle - More prominent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(5.dp)
                            .background(
                                colorScheme.onSurfaceVariantColor.copy(alpha = 0.6f),
                                RoundedCornerShape(3.dp)
                            )
                    )
                }

                // Tab Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            selectedTab = 0
                            onTabClick(0)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (selectedTab == 0) colorScheme.primaryColor else colorScheme.onSurfaceVariantColor
                        )
                    ) {
                        Text(
                            "UP NEXT",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    TextButton(
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (selectedTab == 1) colorScheme.primaryColor else colorScheme.onSurfaceVariantColor
                        )
                    ) {
                        Text(
                            "LYRICS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Content area wrapped with a scrim that hides the first row underneath the peek
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = constraintsMaxHeight - 140.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                when (selectedTab) {
                    0 -> QueueContent(playerState, colorScheme)
                    1 -> LyricsContent(colorScheme)
                }
                // Top gradient scrim to hide first item under the peek
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    colorScheme.surfaceColor.copy(alpha = 1f),
                                    colorScheme.surfaceColor.copy(alpha = 0f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun QueueContent(
    playerState: li.auna.korusmusic.player.PlayerState,
    colorScheme: DynamicColorScheme
) {
    if (playerState.queue.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(playerState.queue) { song ->
                QueueItem(
                    song = song,
                    colorScheme = colorScheme,
                    isCurrentlyPlaying = song == playerState.currentSong
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Queue,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = colorScheme.onSurfaceVariantColor
            )
            Text(
                text = "No songs in queue",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurfaceColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Add songs to see them here",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariantColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun LyricsContent(
    colorScheme: DynamicColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lyrics,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colorScheme.onSurfaceVariantColor
        )
        Text(
            text = "Lyrics not available",
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurfaceColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Lyrics will appear here when available",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariantColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun QueueItem(
    song: li.auna.korusmusic.domain.model.Song,
    colorScheme: DynamicColorScheme,
    isCurrentlyPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isCurrentlyPlaying)
                    colorScheme.primaryColor.copy(alpha = 0.1f)
                else
                    Color.Transparent
            )
            .clickable { /* TODO: Play this song */ }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art
        CoverArtImage(
            song = song,
            size = 48.dp,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrentlyPlaying) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isCurrentlyPlaying) colorScheme.primaryColor else colorScheme.onSurfaceColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${song.artist.name} • ${song.album.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariantColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (isCurrentlyPlaying) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Now Playing",
                tint = colorScheme.primaryColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = song.getDurationFormatted(),
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariantColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}