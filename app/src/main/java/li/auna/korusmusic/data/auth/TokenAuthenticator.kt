package li.auna.korusmusic.data.auth

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

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking(ioDispatcher) {
            val refreshToken = tokenManager.getRefreshToken()
            
            if (refreshToken == null) {
                // No refresh token available, clear tokens and return null to stop retrying
                tokenManager.clearTokens()
                return@runBlocking null
            }

            try {
                // Try to refresh the token
                val apiService = response.request.tag(li.auna.korusmusic.data.network.KorusApiService::class.java)
                if (apiService != null) {
                    val refreshResponse = apiService.refreshToken(
                        li.auna.korusmusic.data.network.dto.RefreshTokenRequest(refreshToken)
                    )
                    
                    // Save the new access token
                    tokenManager.saveAccessToken(refreshResponse.accessToken)
                    
                    // Retry the original request with the new token
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                        .build()
                } else {
                    // Can't refresh without API service, clear tokens
                    tokenManager.clearTokens()
                    return@runBlocking null
                }
            } catch (e: Exception) {
                // Refresh failed, clear tokens and return null to stop retrying
                tokenManager.clearTokens()
                return@runBlocking null
            }
        }
    }
}