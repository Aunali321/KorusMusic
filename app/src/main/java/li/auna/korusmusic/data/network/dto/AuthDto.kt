package li.auna.korusmusic.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: String,
    val user: UserDto
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponse(
    val accessToken: String,
    val expiresAt: String
)

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val email: String? = null,
    val role: String,
    val createdAt: String? = null,
    val lastLogin: String? = null
)