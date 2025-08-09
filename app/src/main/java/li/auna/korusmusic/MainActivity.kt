package li.auna.korusmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.inject
import li.auna.korusmusic.data.DataManager
import li.auna.korusmusic.data.auth.TokenManager
import li.auna.korusmusic.navigation.KorusNavigation
import li.auna.korusmusic.player.PlayerServiceConnection
import li.auna.korusmusic.ui.theme.KorusMusicTheme
import li.auna.korusmusic.ui.theme.Zinc950

class MainActivity : ComponentActivity() {
    
    private val tokenManager: TokenManager by inject()
    private val dataManager: DataManager by inject()
    private val playerServiceConnection: PlayerServiceConnection by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            KorusMusicTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Zinc950),
                    color = Zinc950
                ) {
                    val navController = rememberNavController()
                    val hasTokens by tokenManager.accessToken.collectAsState(initial = null)
                    
                    LaunchedEffect(Unit) {
                        playerServiceConnection.connect()
                    }
                    
                    // Handle automatic logout when tokens are cleared (only when authenticated)
                    LaunchedEffect(hasTokens) {
                        if (hasTokens != null) {
                            tokenManager.logoutEvents.collect {
                                // Navigate to login screen when tokens are cleared
                                navController.navigate(li.auna.korusmusic.navigation.KorusDestination.Login) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                    
                    // Trigger initial data sync when user has valid tokens
                    LaunchedEffect(hasTokens) {
                        if (hasTokens != null) {
                            dataManager.performInitialSync()
                        }
                    }
                    
                    KorusNavigation(
                        navController = navController,
                        tokenManager = tokenManager,
                        playerServiceConnection = playerServiceConnection
                    )
                }
            }
        }
    }
    
    override fun onDestroy() {
        playerServiceConnection.disconnect()
        super.onDestroy()
    }
}