package li.auna.korusmusic.ui.screens.albumdetail

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import li.auna.korusmusic.ui.components.SongItem
import li.auna.korusmusic.player.PlayerServiceConnection
import li.auna.korusmusic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    albumId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    viewModel: AlbumDetailViewModel = koinViewModel(),
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val albumState by viewModel.albumState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Zinc950)
    ) {
        // Glass Top App Bar
        TopAppBar(
            title = { Text("") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondary
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* TODO: More options */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
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
        
        when {
            albumState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentBlue
                    )
                }
            }
            albumState.error != null -> {
                val error = albumState.error
                ErrorContent(
                    error = error ?: "Unknown error",
                    onRetry = { viewModel.refresh() }
                )
            }
            albumState.album != null -> {
                val album = albumState.album ?: return
                AlbumContent(
                    album = album,
                    songs = albumState.songs,
                    onPlayAlbum = {
                        val songs = albumState.songs
                        if (songs.isNotEmpty()) {
                            playerServiceConnection.setQueue(songs, 0)
                        }
                    },
                    onShufflePlay = {
                        val songs = albumState.songs
                        if (songs.isNotEmpty()) {
                            playerServiceConnection.setQueue(songs.shuffled(), 0)
                        }
                    },
                    onSongClick = { song ->
                        val songs = albumState.songs
                        val songIndex = songs.indexOf(song)
                        if (songIndex >= 0) {
                            playerServiceConnection.setQueue(songs, songIndex)
                        }
                    },
                    onNavigateToArtist = onNavigateToArtist
                )
            }
        }
    }
}

@Composable
private fun AlbumContent(
    album: li.auna.korusmusic.domain.model.Album,
    songs: List<li.auna.korusmusic.domain.model.Song>,
    onPlayAlbum: () -> Unit,
    onShufflePlay: () -> Unit,
    onSongClick: (li.auna.korusmusic.domain.model.Song) -> Unit,
    onNavigateToArtist: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Album Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassSurface(shape = RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Album Art Placeholder
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .glassSurfaceVariant(shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TextTertiary
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Album Info
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                album.artist?.let { artist ->
                    TextButton(
                        onClick = { onNavigateToArtist(artist.id) },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = artist.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = AccentBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (album.year != null) {
                        Text(
                            text = album.year.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    
                    if (songs.isNotEmpty()) {
                        Text(
                            text = "${songs.size} songs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    
                    album.getDurationFormatted()?.let { duration ->
                        Text(
                            text = duration,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
        
        // Play Controls
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassSurface(shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onPlayAlbum,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        "Play",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                OutlinedButton(
                    onClick = onShufflePlay,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        "Shuffle",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Songs List
        if (songs.isNotEmpty()) {
            item {
                Text(
                    text = "Songs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            
            items(songs) { song ->
                SongItem(
                    song = song,
                    onClick = { onSongClick(song) },
                    showMoreButton = true
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error loading album",
            style = MaterialTheme.typography.headlineSmall,
            color = AccentRed,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
        
        Button(
            onClick = onRetry,
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
}