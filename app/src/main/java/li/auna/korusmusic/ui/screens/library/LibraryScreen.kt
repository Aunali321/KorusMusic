package li.auna.korusmusic.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import li.auna.korusmusic.ui.components.SongItem
import li.auna.korusmusic.player.PlayerServiceConnection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlaylist: (Long) -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    viewModel: LibraryViewModel = koinViewModel(),
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val libraryState by viewModel.libraryState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = listOf("Songs", "Albums", "Artists", "Playlists")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Your Library",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> SongsTab(
                songs = libraryState.songs,
                isLoading = libraryState.isLoading,
                error = libraryState.error,
                onSongClick = { song ->
                    val songIndex = libraryState.songs.indexOf(song)
                    playerServiceConnection.setQueue(libraryState.songs, songIndex)
                },
                onRetry = { viewModel.refresh() }
            )
            1 -> AlbumsTab(
                albums = libraryState.albums,
                isLoading = libraryState.isLoading,
                error = libraryState.error,
                onAlbumClick = onNavigateToAlbum,
                onRetry = { viewModel.refresh() }
            )
            2 -> ArtistsTab(
                artists = libraryState.artists,
                isLoading = libraryState.isLoading,
                error = libraryState.error,
                onArtistClick = onNavigateToArtist,
                onRetry = { viewModel.refresh() }
            )
            3 -> PlaylistsTab(
                playlists = libraryState.playlists,
                isLoading = libraryState.isLoading,
                error = libraryState.error,
                onPlaylistClick = onNavigateToPlaylist,
                onRetry = { viewModel.refresh() }
            )
        }
    }
}

@Composable
private fun SongsTab(
    songs: List<li.auna.korusmusic.domain.model.Song>,
    isLoading: Boolean,
    error: String?,
    onSongClick: (li.auna.korusmusic.domain.model.Song) -> Unit,
    onRetry: () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            ErrorContent(error = error, onRetry = onRetry)
        }
        songs.isEmpty() -> {
            EmptyContent(message = "No songs in your library")
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(songs) { song ->
                    SongItem(
                        song = song,
                        onClick = { onSongClick(song) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumsTab(
    albums: List<li.auna.korusmusic.domain.model.Album>,
    isLoading: Boolean,
    error: String?,
    onAlbumClick: (Long) -> Unit,
    onRetry: () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            ErrorContent(error = error, onRetry = onRetry)
        }
        albums.isEmpty() -> {
            EmptyContent(message = "No albums in your library")
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(albums) { album ->
                    AlbumItem(
                        album = album,
                        onClick = { onAlbumClick(album.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistsTab(
    artists: List<li.auna.korusmusic.domain.model.Artist>,
    isLoading: Boolean,
    error: String?,
    onArtistClick: (Long) -> Unit,
    onRetry: () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            ErrorContent(error = error, onRetry = onRetry)
        }
        artists.isEmpty() -> {
            EmptyContent(message = "No artists in your library")
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(artists) { artist ->
                    ArtistItem(
                        artist = artist,
                        onClick = { onArtistClick(artist.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistsTab(
    playlists: List<li.auna.korusmusic.domain.model.Playlist>,
    isLoading: Boolean,
    error: String?,
    onPlaylistClick: (Long) -> Unit,
    onRetry: () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            ErrorContent(error = error, onRetry = onRetry)
        }
        playlists.isEmpty() -> {
            EmptyContent(message = "No playlists in your library")
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(playlists) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist.id) }
                    )
                }
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error loading library",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Placeholder components - these would be implemented with proper UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumItem(
    album: li.auna.korusmusic.domain.model.Album,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = album.name, fontWeight = FontWeight.Medium)
            Text(
                text = album.artist?.name ?: "Unknown Artist",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistItem(
    artist: li.auna.korusmusic.domain.model.Artist,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = artist.name, fontWeight = FontWeight.Medium)
            Text(
                text = "${artist.albumCount} albums",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistItem(
    playlist: li.auna.korusmusic.domain.model.Playlist,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = playlist.name, fontWeight = FontWeight.Medium)
            Text(
                text = "${playlist.songCount} songs",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}