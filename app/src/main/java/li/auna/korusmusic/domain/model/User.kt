package li.auna.korusmusic.domain.model

data class User(
    val id: Long,
    val username: String,
    val email: String? = null,
    val role: String,
    val createdAt: String? = null,
    val lastLogin: String? = null
)