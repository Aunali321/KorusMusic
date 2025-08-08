package li.auna.korusmusic.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import org.koin.androidx.compose.koinViewModel
import li.auna.korusmusic.data.auth.TokenManager
import li.auna.korusmusic.ui.components.BottomNavigationBar
import li.auna.korusmusic.ui.components.MiniPlayer
import li.auna.korusmusic.ui.screens.home.HomeScreen
import li.auna.korusmusic.ui.screens.library.LibraryScreen
import li.auna.korusmusic.ui.screens.login.LoginScreen
import li.auna.korusmusic.ui.screens.nowplaying.NowPlayingScreen
import li.auna.korusmusic.ui.screens.search.SearchScreen
import li.auna.korusmusic.ui.screens.settings.SettingsScreen
import li.auna.korusmusic.ui.screens.albumdetail.AlbumDetailScreen
import li.auna.korusmusic.ui.screens.artistdetail.ArtistDetailScreen
import li.auna.korusmusic.ui.screens.playlistdetail.PlaylistDetailScreen
import li.auna.korusmusic.player.PlayerServiceConnection

@Composable
fun KorusNavigation(
    navController: NavHostController,
    tokenManager: TokenManager,
    playerServiceConnection: PlayerServiceConnection = org.koin.androidx.compose.get()
) {
    val hasTokens by tokenManager.accessToken.collectAsState(initial = null)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    
    // Get player state for mini-player visibility
    val playerManager by playerServiceConnection.playerManager.collectAsState()
    val playerState by (playerManager?.playerState?.collectAsState() ?: mutableStateOf(li.auna.korusmusic.player.PlayerState()))
    
    val startDestination = if (hasTokens != null) {
        KorusDestination.Home
    } else {
        KorusDestination.Login
    }
    
    // Determine if we should show bottom navigation
    val showBottomNav = hasTokens != null && when (currentBackStackEntry?.destination?.route) {
        KorusDestination.Home::class.qualifiedName -> true
        KorusDestination.Search::class.qualifiedName -> true
        KorusDestination.Library::class.qualifiedName -> true
        KorusDestination.Settings::class.qualifiedName -> true
        else -> false
    }
    
    // Determine if we should show mini-player
    val isNowPlayingScreen = currentBackStackEntry?.destination?.route == KorusDestination.NowPlaying::class.qualifiedName
    val showMiniPlayer = hasTokens != null && 
                        !isNowPlayingScreen && 
                        playerState.currentSong != null
    
    // Calculate bottom padding based on what's showing
    val bottomPadding = when {
        showBottomNav && showMiniPlayer -> 120.dp // Both bottom nav (60dp) + mini player (60dp)
        showBottomNav -> 60.dp // Just bottom nav
        showMiniPlayer -> 60.dp // Just mini player  
        else -> 0.dp // Neither
    }
    
    // Determine current destination for bottom nav highlighting
    val currentDestination = when (currentBackStackEntry?.destination?.route) {
        KorusDestination.Home::class.qualifiedName -> KorusDestination.Home
        KorusDestination.Search::class.qualifiedName -> KorusDestination.Search
        KorusDestination.Library::class.qualifiedName -> KorusDestination.Library
        KorusDestination.Settings::class.qualifiedName -> KorusDestination.Settings
        else -> KorusDestination.Home
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
        composable<KorusDestination.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(KorusDestination.Home) {
                        popUpTo(KorusDestination.Login) { inclusive = true }
                    }
                }
            )
        }
        
        composable<KorusDestination.Home> {
            HomeScreen(
                onNavigateToLibrary = { navController.navigate(KorusDestination.Library) },
                onNavigateToSearch = { navController.navigate(KorusDestination.Search) },
                onNavigateToNowPlaying = { navController.navigate(KorusDestination.NowPlaying) },
                onNavigateToSettings = { navController.navigate(KorusDestination.Settings) },
                onNavigateToAlbum = { albumId -> 
                    navController.navigate(KorusDestination.AlbumDetail(albumId))
                },
                onNavigateToArtist = { artistId ->
                    navController.navigate(KorusDestination.ArtistDetail(artistId))
                }
            )
        }
        
        composable<KorusDestination.Library> {
            LibraryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlaylist = { playlistId ->
                    navController.navigate(KorusDestination.PlaylistDetail(playlistId))
                },
                onNavigateToAlbum = { albumId ->
                    navController.navigate(KorusDestination.AlbumDetail(albumId)) 
                },
                onNavigateToArtist = { artistId ->
                    navController.navigate(KorusDestination.ArtistDetail(artistId))
                }
            )
        }
        
        composable<KorusDestination.Search> {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAlbum = { albumId ->
                    navController.navigate(KorusDestination.AlbumDetail(albumId))
                },
                onNavigateToArtist = { artistId ->
                    navController.navigate(KorusDestination.ArtistDetail(artistId))
                }
            )
        }
        
        composable<KorusDestination.NowPlaying> {
            NowPlayingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<KorusDestination.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(KorusDestination.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Detail screens
        composable<KorusDestination.AlbumDetail> { backStackEntry ->
            val albumDetail = backStackEntry.arguments?.let {
                navController.currentBackStackEntry?.arguments?.let { args ->
                    args.getLong("albumId")
                } ?: (backStackEntry.destination.route?.let { route ->
                    // Extract albumId from route if available
                    route.substringAfter("albumId=").substringBefore("}")
                        .toLongOrNull() ?: 0L
                } ?: 0L)
            } ?: 0L
            
            AlbumDetailScreen(
                albumId = albumDetail,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArtist = { artistId ->
                    navController.navigate(KorusDestination.ArtistDetail(artistId))
                }
            )
        }
        
        composable<KorusDestination.ArtistDetail> { backStackEntry ->
            val artistDetail = backStackEntry.arguments?.let {
                navController.currentBackStackEntry?.arguments?.let { args ->
                    args.getLong("artistId")
                } ?: (backStackEntry.destination.route?.let { route ->
                    route.substringAfter("artistId=").substringBefore("}")
                        .toLongOrNull() ?: 0L
                } ?: 0L)
            } ?: 0L
            
            ArtistDetailScreen(
                artistId = artistDetail,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAlbum = { albumId ->
                    navController.navigate(KorusDestination.AlbumDetail(albumId))
                }
            )
        }
        
        composable<KorusDestination.PlaylistDetail> { backStackEntry ->
            val playlistDetail = backStackEntry.arguments?.let {
                navController.currentBackStackEntry?.arguments?.let { args ->
                    args.getLong("playlistId")
                } ?: (backStackEntry.destination.route?.let { route ->
                    route.substringAfter("playlistId=").substringBefore("}")
                        .toLongOrNull() ?: 0L
                } ?: 0L)
            } ?: 0L
            
            PlaylistDetailScreen(
                playlistId = playlistDetail,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        }
        
        // Bottom UI Components - Stack them properly from bottom to top
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            // Mini Player (above bottom nav)
            if (showMiniPlayer) {
                MiniPlayer(
                    playerServiceConnection = playerServiceConnection,
                    onExpandToNowPlaying = {
                        navController.navigate(KorusDestination.NowPlaying)
                    }
                )
            }
            
            // Bottom Navigation (at the very bottom)
            if (showBottomNav) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    onNavigateToDestination = { destination ->
                        navController.navigate(destination) {
                            // Pop up to start destination to avoid building up a large stack
                            popUpTo(KorusDestination.Home) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}