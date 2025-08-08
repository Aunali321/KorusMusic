package li.auna.korusmusic.ui.screens.artistdetail

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import li.auna.korusmusic.ui.components.SongItem
import li.auna.korusmusic.player.PlayerServiceConnection
import li.auna.korusmusic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    viewModel: ArtistDetailViewModel = koinViewModel(),
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val artistState by viewModel.artistState.collectAsState()
    
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
            artistState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentBlue
                    )
                }
            }
            artistState.error != null -> {
                val error = artistState.error
                ErrorContent(
                    error = error ?: "Unknown error",
                    onRetry = { viewModel.refresh() }
                )
            }
            artistState.artist != null -> {
                val artist = artistState.artist ?: return
                ArtistContent(
                    artist = artist,
                    albums = artistState.albums,
                    topTracks = artistState.topTracks,
                    onPlayTopTracks = {
                        val topTracks = artistState.topTracks
                        if (topTracks.isNotEmpty()) {
                            playerServiceConnection.setQueue(topTracks, 0)
                        }
                    },
                    onShufflePlay = {
                        val topTracks = artistState.topTracks
                        if (topTracks.isNotEmpty()) {
                            playerServiceConnection.setQueue(topTracks.shuffled(), 0)
                        }
                    },
                    onSongClick = { song ->
                        val topTracks = artistState.topTracks
                        val songIndex = topTracks.indexOf(song)
                        if (songIndex >= 0) {
                            playerServiceConnection.setQueue(topTracks, songIndex)
                        }
                    },
                    onAlbumClick = onNavigateToAlbum
                )
            }
        }
    }
}

@Composable
private fun ArtistContent(
    artist: li.auna.korusmusic.domain.model.Artist,
    albums: List<li.auna.korusmusic.domain.model.Album>,
    topTracks: List<li.auna.korusmusic.domain.model.Song>,
    onPlayTopTracks: () -> Unit,
    onShufflePlay: () -> Unit,
    onSongClick: (li.auna.korusmusic.domain.model.Song) -> Unit,
    onAlbumClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Artist Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassSurface(shape = RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Artist Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .glassSurfaceVariant(shape = RoundedCornerShape(80.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextTertiary
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Artist Info
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${artist.albumCount} albums",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "${artist.songCount} songs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
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
                    onClick = onPlayTopTracks,
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
        
        // Top Tracks Section
        if (topTracks.isNotEmpty()) {
                item {
                    Text(
                        text = "Popular",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                
                items(topTracks.take(5)) { song ->
                    SongItem(
                        song = song,
                        onClick = { onSongClick(song) },
                        showMoreButton = true
                    )
                }
                
                if (topTracks.size > 5) {
                    item {
                        TextButton(
                            onClick = { /* TODO: Show all tracks */ },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                "Show ${topTracks.size - 5} more songs",
                                color = AccentBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
        }
        
        // Albums Section
        if (albums.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Albums",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        
                        if (albums.size > 3) {
                            TextButton(onClick = { /* TODO: Show all albums */ }) {
                                Text(
                                    "See All",
                                    color = AccentBlue,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(albums.take(6)) { album ->
                            AlbumCard(
                                album = album,
                                onClick = { onAlbumClick(album.id) }
                            )
                        }
                    }
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumCard(
    album: li.auna.korusmusic.domain.model.Album,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = glassCardColors(),
        elevation = glassCardElevation(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Album Art Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .glassSurfaceVariant(shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = TextTertiary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = album.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            album.year?.let { year ->
                Text(
                    text = year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(top = 4.dp)
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
            text = "Error loading artist",
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