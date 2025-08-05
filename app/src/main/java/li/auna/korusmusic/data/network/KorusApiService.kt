package li.auna.korusmusic.data.network

import li.auna.korusmusic.data.network.dto.*
import retrofit2.http.*

interface KorusApiService {
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: RefreshTokenRequest)

    @GET("me")
    suspend fun getCurrentUser(): UserDto

    // Library Stats
    @GET("library/stats")
    suspend fun getLibraryStats(): LibraryStatsDto

    // Artists
    @GET("artists")
    suspend fun getArtists(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String? = null
    ): List<ArtistDto>

    @GET("artists/{id}")
    suspend fun getArtist(@Path("id") artistId: Long): ArtistDto

    // Albums
    @GET("albums")
    suspend fun getAlbums(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String? = null,
        @Query("year") year: Int? = null
    ): List<AlbumDto>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") albumId: Long): AlbumDto

    @GET("albums/{id}/songs")
    suspend fun getAlbumSongs(@Path("id") albumId: Long): List<SongDto>

    // Songs
    @GET("songs")
    suspend fun getSongs(@Query("ids") ids: String): List<SongDto>

    @GET("songs/{id}")
    suspend fun getSong(@Path("id") songId: Long): SongDto

    // Search
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): SearchResponse

    // Playlists
    @GET("playlists")
    suspend fun getPlaylists(): List<PlaylistDto>

    @POST("playlists")
    suspend fun createPlaylist(@Body request: CreatePlaylistRequest): PlaylistDto

    @GET("playlists/{id}")
    suspend fun getPlaylist(@Path("id") playlistId: Long): PlaylistDto

    @PUT("playlists/{id}")
    suspend fun updatePlaylist(
        @Path("id") playlistId: Long,
        @Body request: UpdatePlaylistRequest
    ): PlaylistDto

    @DELETE("playlists/{id}")
    suspend fun deletePlaylist(@Path("id") playlistId: Long)

    @POST("playlists/{id}/songs")
    suspend fun addSongsToPlaylist(
        @Path("id") playlistId: Long,
        @Body request: AddSongsToPlaylistRequest
    )

    @DELETE("playlists/{id}/songs")
    suspend fun removeSongsFromPlaylist(
        @Path("id") playlistId: Long,
        @Body request: RemoveSongsFromPlaylistRequest
    )

    @PUT("playlists/{id}/reorder")
    suspend fun reorderPlaylist(
        @Path("id") playlistId: Long,
        @Body request: ReorderPlaylistRequest
    )

    // User Library
    @GET("me/library/liked/songs")
    suspend fun getLikedSongs(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<SongDto>

    @GET("me/library/liked/albums")
    suspend fun getLikedAlbums(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<AlbumDto>

    @GET("me/library/followed/artists")
    suspend fun getFollowedArtists(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<ArtistDto>

    @POST("me/library/like/songs")
    suspend fun likeSongs(@Body request: AddSongsToPlaylistRequest)

    @DELETE("me/library/unlike/songs")
    suspend fun unlikeSongs(@Body request: RemoveSongsFromPlaylistRequest)

    @POST("me/library/like/albums/{id}")
    suspend fun likeAlbum(@Path("id") albumId: Long)

    @DELETE("me/library/unlike/albums/{id}")
    suspend fun unlikeAlbum(@Path("id") albumId: Long)

    @POST("me/library/follow/artists/{id}")
    suspend fun followArtist(@Path("id") artistId: Long)

    @DELETE("me/library/unfollow/artists/{id}")
    suspend fun unfollowArtist(@Path("id") artistId: Long)

    // History & Statistics
    @POST("me/history/play")
    suspend fun recordPlay(@Body request: RecordPlayRequest)

    @GET("me/history/recent")
    suspend fun getRecentHistory(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<PlayHistoryDto>

    @GET("me/stats")
    suspend fun getUserStats(): UserStatsDto

    @GET("me/home")
    suspend fun getHomeData(): HomeDataDto
}

// Additional DTOs for library stats and home data
@kotlinx.serialization.Serializable
data class LibraryStatsDto(
    val totalSongs: Int,
    val totalArtists: Int,
    val totalAlbums: Int,
    val totalDuration: Int
)

@kotlinx.serialization.Serializable
data class HomeDataDto(
    @kotlinx.serialization.SerialName("recent_plays") val recentPlays: List<SongDto> = emptyList(),
    @kotlinx.serialization.SerialName("recommended_songs") val recommendedSongs: List<SongDto> = emptyList(),
    @kotlinx.serialization.SerialName("recommended_albums") val recommendedAlbums: List<AlbumDto> = emptyList(),
    @kotlinx.serialization.SerialName("recently_added") val recentlyAdded: List<SongDto> = emptyList()
)