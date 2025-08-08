package li.auna.korusmusic.ui.screens.search

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import li.auna.korusmusic.ui.components.SongItem
import li.auna.korusmusic.ui.theme.*
import li.auna.korusmusic.player.PlayerServiceConnection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val searchState by viewModel.searchState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Zinc950)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Search",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
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

        // Glass Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .glassSurface(shape = RoundedCornerShape(16.dp))
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        viewModel.search(it)
                    } else {
                        viewModel.clearSearch()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search songs, artists, albums...",
                        color = TextTertiary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextTertiary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                viewModel.clearSearch()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextTertiary
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentBlue,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                singleLine = true
            )
        }

        when {
            searchQuery.isEmpty() -> {
                EmptySearchState()
            }
            searchState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentBlue
                    )
                }
            }
            searchState.error != null -> {
                val error = searchState.error
                ErrorSearchState(
                    error = error ?: "Unknown error",
                    onRetry = { viewModel.search(searchQuery) }
                )
            }
            else -> {
                SearchResultsContent(
                    searchResults = searchState.searchResults,
                    onSongClick = { song, songs ->
                        val songIndex = songs.indexOf(song)
                        playerServiceConnection.setQueue(songs, songIndex)
                    },
                    onAlbumClick = onNavigateToAlbum,
                    onArtistClick = onNavigateToArtist
                )
            }
        }
    }
}

@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextTertiary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Search for your favorite music",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Find songs, albums, and artists",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ErrorSearchState(
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
            text = "Search Error",
            style = MaterialTheme.typography.headlineSmall,
            color = AccentRed,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
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

@Composable
private fun SearchResultsContent(
    searchResults: SearchResults,
    onSongClick: (li.auna.korusmusic.domain.model.Song, List<li.auna.korusmusic.domain.model.Song>) -> Unit,
    onAlbumClick: (Long) -> Unit,
    onArtistClick: (Long) -> Unit
) {
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Songs Section
        if (searchResults.songs.isNotEmpty()) {
            item {
                Text(
                    text = "Songs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            items(searchResults.songs.take(5)) { song ->
                SongItem(
                    song = song,
                    onClick = { onSongClick(song, searchResults.songs) }
                )
            }
            
            if (searchResults.songs.size > 5) {
                item {
                    TextButton(
                        onClick = { /* TODO: Show all songs */ },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            "Show ${searchResults.songs.size - 5} more songs",
                            color = AccentBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Albums Section
        if (searchResults.albums.isNotEmpty()) {
            item {
                Text(
                    text = "Albums",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            items(searchResults.albums.take(3)) { album ->
                AlbumSearchItem(
                    album = album,
                    onClick = { onAlbumClick(album.id) }
                )
            }
        }
        
        // Artists Section
        if (searchResults.artists.isNotEmpty()) {
            item {
                Text(
                    text = "Artists",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            items(searchResults.artists.take(3)) { artist ->
                ArtistSearchItem(
                    artist = artist,
                    onClick = { onArtistClick(artist.id) }
                )
            }
        }
        
        // No results message
        if (searchResults.songs.isEmpty() && searchResults.albums.isEmpty() && searchResults.artists.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumSearchItem(
    album: li.auna.korusmusic.domain.model.Album,
    onClick: () -> Unit
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .glassSurfaceVariant(shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Album,
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
                    text = album.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = album.artist?.name ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistSearchItem(
    artist: li.auna.korusmusic.domain.model.Artist,
    onClick: () -> Unit
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .glassSurfaceVariant(shape = RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
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
                    text = artist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${artist.albumCount} albums",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}