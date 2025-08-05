package li.auna.korusmusic.domain.repository

import li.auna.korusmusic.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): User
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun refreshToken(): Boolean
}