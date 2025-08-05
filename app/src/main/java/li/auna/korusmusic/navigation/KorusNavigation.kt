package li.auna.korusmusic.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import li.auna.korusmusic.data.auth.TokenManager
import li.auna.korusmusic.ui.screens.home.HomeScreen
import li.auna.korusmusic.ui.screens.library.LibraryScreen
import li.auna.korusmusic.ui.screens.login.LoginScreen
import li.auna.korusmusic.ui.screens.nowplaying.NowPlayingScreen
import li.auna.korusmusic.ui.screens.search.SearchScreen
import li.auna.korusmusic.ui.screens.settings.SettingsScreen

@Composable
fun KorusNavigation(
    navController: NavHostController,
    tokenManager: TokenManager
) {
    val hasTokens by tokenManager.accessToken.collectAsState(initial = null)
    
    val startDestination = if (hasTokens != null) {
        KorusDestination.Home
    } else {
        KorusDestination.Login
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
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
        
        // TODO: Add detail screens
        composable<KorusDestination.AlbumDetail> { backStackEntry ->
            // AlbumDetailScreen(albumId = backStackEntry.arguments?.getLong("albumId") ?: 0L)
        }
        
        composable<KorusDestination.ArtistDetail> { backStackEntry ->
            // ArtistDetailScreen(artistId = backStackEntry.arguments?.getLong("artistId") ?: 0L)
        }
        
        composable<KorusDestination.PlaylistDetail> { backStackEntry ->
            // PlaylistDetailScreen(playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L)
        }
    }
}