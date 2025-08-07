package li.auna.korusmusic.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.ui.components.SongItem
import li.auna.korusmusic.player.PlayerServiceConnection
import li.auna.korusmusic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLibrary: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNowPlaying: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val homeState by viewModel.homeState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Zinc950)
    ) {
        // Glass Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Korus Music",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            actions = {
                IconButton(onClick = onNavigateToSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary
                    )
                }
            },
            modifier = Modifier.glassTopAppBar(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GlassSurface,
                titleContentColor = TextPrimary
            )
        )

        if (homeState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AccentBlue
                )
            }
        } else if (homeState.error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error loading home data",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AccentRed,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = homeState.error ?: "Unknown error",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .glassSurface(shape = RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("Retry", fontWeight = FontWeight.Medium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recently Played Section
                if (homeState.homeData?.recentlyPlayed?.isNotEmpty() == true) {
                    item {
                        HomeSection(
                            title = "Recently Played",
                            songs = homeState.homeData?.recentlyPlayed ?: emptyList(),
                            onSongClick = { song, songs ->
                                val songIndex = songs.indexOf(song)
                                playerServiceConnection.setQueue(songs, songIndex)
                            },
                            onSeeAllClick = { /* Navigate to recently played */ }
                        )
                    }
                }

                // Recently Added Section
                if (homeState.homeData?.recentlyAdded?.isNotEmpty() == true) {
                    item {
                        HomeSection(
                            title = "Recently Added",
                            songs = homeState.homeData?.recentlyAdded ?: emptyList(),
                            onSongClick = { song, songs ->
                                val songIndex = songs.indexOf(song)
                                playerServiceConnection.setQueue(songs, songIndex)
                            },
                            onSeeAllClick = { /* Navigate to recently added */ }
                        )
                    }
                }

                // Quick Actions
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToLibrary,
                        colors = glassCardColors(),
                        elevation = glassCardElevation(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LibraryMusic,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Your Library",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Browse your music collection",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextTertiary
                            )
                        }
                    }
                }
            }
        }

        // Mini Player (if song is playing)
        val playerManager by playerServiceConnection.playerManager.collectAsState()
        playerManager?.let { manager ->
            val playerState by manager.playerState.collectAsState()
            playerState.currentSong?.let { currentSong ->
                MiniPlayer(
                    song = currentSong,
                    isPlaying = playerState.isPlaying,
                    onPlayPause = { playerServiceConnection.togglePlayPause() },
                    onExpand = onNavigateToNowPlaying,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun HomeSection(
    title: String,
    songs: List<Song>,
    onSongClick: (Song, List<Song>) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            TextButton(onClick = onSeeAllClick) {
                Text(
                    "See All",
                    color = AccentBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(songs.take(10)) { song ->
                SongItem(
                    song = song,
                    onClick = { onSongClick(song, songs) },
                    modifier = Modifier.width(300.dp)
                )
            }
        }
    }
}

@Composable
private fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onExpand,
        colors = glassCardColors(),
        elevation = glassCardElevation(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = song.artist.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1
                )
            }
            
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(48.dp)
                    .glassSurfaceVariant(shape = RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}