package li.auna.korusmusic.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class KorusDestination {
    @Serializable
    data object Login : KorusDestination()
    
    @Serializable
    data object Home : KorusDestination()
    
    @Serializable
    data object Library : KorusDestination()
    
    @Serializable
    data object Search : KorusDestination()
    
    @Serializable
    data object NowPlaying : KorusDestination()
    
    @Serializable
    data object Settings : KorusDestination()
    
    @Serializable
    data class AlbumDetail(val albumId: Long) : KorusDestination()
    
    @Serializable
    data class ArtistDetail(val artistId: Long) : KorusDestination()
    
    @Serializable
    data class PlaylistDetail(val playlistId: Long) : KorusDestination()
}