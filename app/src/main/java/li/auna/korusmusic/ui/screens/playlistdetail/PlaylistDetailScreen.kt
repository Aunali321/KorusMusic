package li.auna.korusmusic.ui.screens.playlistdetail

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
fun PlaylistDetailScreen(
    playlistId: Long,
    onNavigateBack: () -> Unit,
    viewModel: PlaylistDetailViewModel = koinViewModel(),
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val playlistState by viewModel.playlistState.collectAsState()
    
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
            playlistState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentBlue
                    )
                }
            }
            playlistState.error != null -> {
                val error = playlistState.error
                ErrorContent(
                    error = error ?: "Unknown error",
                    onRetry = { viewModel.refresh() }
                )
            }
            playlistState.playlist != null -> {
                val playlist = playlistState.playlist ?: return
                val playlistSongs = playlistState.playlistSongs
                PlaylistContent(
                    playlist = playlist,
                    playlistSongs = playlistSongs,
                    onPlayPlaylist = {
                        val songs = playlistSongs.map { it.song }
                        if (songs.isNotEmpty()) {
                            playerServiceConnection.setQueue(songs, 0)
                        }
                    },
                    onShufflePlay = {
                        val songs = playlistSongs.map { it.song }
                        if (songs.isNotEmpty()) {
                            playerServiceConnection.setQueue(songs.shuffled(), 0)
                        }
                    },
                    onSongClick = { song ->
                        val songs = playlistSongs.map { it.song }
                        val songIndex = songs.indexOf(song)
                        if (songIndex >= 0) {
                            playerServiceConnection.setQueue(songs, songIndex)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaylistContent(
    playlist: li.auna.korusmusic.domain.model.Playlist,
    playlistSongs: List<li.auna.korusmusic.domain.model.PlaylistSong>,
    onPlayPlaylist: () -> Unit,
    onShufflePlay: () -> Unit,
    onSongClick: (li.auna.korusmusic.domain.model.Song) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Playlist Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassSurface(shape = RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Playlist Art Placeholder
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .glassSurfaceVariant(shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistPlay,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = TextTertiary
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Playlist Info
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                playlist.description?.let { description ->
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                playlist.owner?.let { owner ->
                    Text(
                        text = "By ${owner.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (playlistSongs.isNotEmpty()) {
                        Text(
                            text = "${playlistSongs.size} songs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    
                    playlist.getDurationFormatted()?.let { duration ->
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
                    onClick = onPlayPlaylist,
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
                
                OutlinedButton(
                    onClick = { /* TODO: Add songs to playlist */ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        // Songs List
        if (playlistSongs.isNotEmpty()) {
            item {
                Text(
                    text = "Songs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            
            items(playlistSongs) { playlistSong ->
                PlaylistSongItem(
                    playlistSong = playlistSong,
                    onClick = { onSongClick(playlistSong.song) },
                    onRemove = { /* TODO: Remove from playlist */ }
                )
            }
        } else {
            item {
                EmptyPlaylistContent(
                    onAddSongs = { /* TODO: Add songs to playlist */ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistSongItem(
    playlistSong: li.auna.korusmusic.domain.model.PlaylistSong,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = glassCardColors(),
        elevation = glassCardElevation(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Track Position
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .glassSurfaceVariant(shape = RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (playlistSong.position + 1).toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Album art placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .glassSurfaceVariant(shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = playlistSong.song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${playlistSong.song.artist.name} • ${playlistSong.song.album.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = playlistSong.song.getDurationFormatted(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = TextTertiary
                )
            }
        }
    }
}

@Composable
private fun EmptyPlaylistContent(
    onAddSongs: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassSurface(shape = RoundedCornerShape(16.dp))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextTertiary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This playlist is empty",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        
        Text(
            text = "Add some songs to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Button(
            onClick = onAddSongs,
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                contentColor = TextPrimary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp)
            )
            Text(
                "Add Songs",
                fontWeight = FontWeight.Medium
            )
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
            text = "Error loading playlist",
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