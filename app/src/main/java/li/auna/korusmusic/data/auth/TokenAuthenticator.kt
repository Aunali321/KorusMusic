package li.auna.korusmusic.data.auth

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val ioDispatcher: CoroutineDispatcher
) : Authenticator {
    
    companion object {
        private const val TAG = "TokenAuthenticator"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        return try {
            runBlocking(ioDispatcher) {
                Log.w(TAG, "401 received - clearing tokens")
                tokenManager.clearTokens()
                null // Don't retry, let the UI handle logout
            }
        } catch (e: Exception) {
            Log.e(TAG, "Authenticator failed: ${e.message}")
            null
        }
    }
}