package li.auna.korusmusic.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import li.auna.korusmusic.data.mapper.toDomainModel
import li.auna.korusmusic.data.network.KorusApiService
import li.auna.korusmusic.data.network.dto.LoginRequest
import li.auna.korusmusic.data.network.dto.RefreshTokenRequest
import li.auna.korusmusic.domain.model.User
import li.auna.korusmusic.domain.repository.AuthRepository
import li.auna.korusmusic.data.auth.TokenManager

class AuthRepositoryImpl(
    private val apiService: KorusApiService,
    private val tokenManager: TokenManager,
    private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    override suspend fun login(username: String, password: String): User =
        withContext(ioDispatcher) {
            val response = apiService.login(LoginRequest(username, password))
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            response.user.toDomainModel()
        }

    override suspend fun logout() {
        withContext(ioDispatcher) {
            try {
                val refreshToken = tokenManager.getRefreshToken()
                if (refreshToken != null) {
                    apiService.logout(RefreshTokenRequest(refreshToken))
                }
            } catch (e: Exception) {
                // Even if logout fails on server, clear local tokens
            } finally {
                tokenManager.clearTokens()
            }
        }
    }

    override suspend fun getCurrentUser(): User? =
        withContext(ioDispatcher) {
            try {
                val userDto = apiService.getCurrentUser()
                userDto.toDomainModel()
            } catch (e: Exception) {
                null
            }
        }

    override suspend fun refreshToken(): Boolean =
        withContext(ioDispatcher) {
            try {
                val refreshToken = tokenManager.getRefreshToken() ?: return@withContext false
                val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
                tokenManager.saveAccessToken(response.accessToken)
                true
            } catch (e: Exception) {
                tokenManager.clearTokens()
                false
            }
        }
}