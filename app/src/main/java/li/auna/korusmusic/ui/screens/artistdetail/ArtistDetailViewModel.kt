package li.auna.korusmusic.ui.screens.artistdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Album
import li.auna.korusmusic.domain.model.Artist
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.AlbumRepository
import li.auna.korusmusic.domain.repository.ArtistRepository
import li.auna.korusmusic.domain.repository.SongRepository
import li.auna.korusmusic.player.PlayerManager

class ArtistDetailViewModel(
    private val artistId: Long,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val _artistDetailState = MutableStateFlow(ArtistDetailState())
    val artistDetailState: StateFlow<ArtistDetailState> = _artistDetailState.asStateFlow()

    init {
        loadArtistDetails()
    }

    private fun loadArtistDetails() {
        viewModelScope.launch {
            _artistDetailState.value = _artistDetailState.value.copy(isLoading = true)
            
            try {
                val artist = artistRepository.getArtist(artistId)
                
                if (artist != null) {
                    // Collect data from multiple repositories
                    combine(
                        albumRepository.getAlbumsByArtist(artistId),
                        songRepository.getSongsByArtist(artistId)
                    ) { albums, songs ->
                        ArtistDetailState(
                            isLoading = false,
                            artist = artist,
                            albums = albums,
                            topTracks = songs.sortedByDescending { it.playCount }.take(10)
                        )
                    }.collect { newState ->
                        _artistDetailState.value = newState
                    }
                } else {
                    _artistDetailState.value = _artistDetailState.value.copy(
                        isLoading = false,
                        error = "Artist not found"
                    )
                }
            } catch (e: Exception) {
                _artistDetailState.value = _artistDetailState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load artist details"
                )
            }
        }
    }

    fun playTopTracks() {
        val topTracks = _artistDetailState.value.topTracks
        if (topTracks.isNotEmpty()) {
            playerManager.setQueue(topTracks, 0)
            playerManager.play()
        }
    }

    fun shuffleAllSongs() {
        viewModelScope.launch {
            try {
                val allSongs = songRepository.getSongsByArtist(artistId)
                allSongs.collect { songs ->
                    if (songs.isNotEmpty()) {
                        playerManager.setQueue(songs.shuffled(), 0)
                        playerManager.play()
                    }
                }
            } catch (e: Exception) {
                _artistDetailState.value = _artistDetailState.value.copy(
                    error = e.message ?: "Failed to play artist songs"
                )
            }
        }
    }

    fun playSong(song: Song) {
        val topTracks = _artistDetailState.value.topTracks
        val index = topTracks.indexOf(song)
        if (index >= 0) {
            playerManager.setQueue(topTracks, index)
            playerManager.play()
        } else {
            // If not in top tracks, play just this song
            playerManager.setQueue(listOf(song), 0)
            playerManager.play()
        }
    }

    fun playAlbum(album: Album) {
        viewModelScope.launch {
            try {
                val songs = songRepository.getSongsByAlbum(album.id)
                songs.collect { songList ->
                    if (songList.isNotEmpty()) {
                        playerManager.setQueue(songList, 0)
                        playerManager.play()
                    }
                }
            } catch (e: Exception) {
                _artistDetailState.value = _artistDetailState.value.copy(
                    error = e.message ?: "Failed to play album"
                )
            }
        }
    }

    fun addSongToQueue(song: Song) {
        playerManager.addToQueue(song)
    }

    fun followArtist() {
        viewModelScope.launch {
            try {
                artistRepository.followArtist(artistId)
                // The artist state will be updated through the repository flow
            } catch (e: Exception) {
                _artistDetailState.value = _artistDetailState.value.copy(
                    error = e.message ?: "Failed to follow artist"
                )
            }
        }
    }

    fun unfollowArtist() {
        viewModelScope.launch {
            try {
                artistRepository.unfollowArtist(artistId)
                // The artist state will be updated through the repository flow
            } catch (e: Exception) {
                _artistDetailState.value = _artistDetailState.value.copy(
                    error = e.message ?: "Failed to unfollow artist"
                )
            }
        }
    }

    fun likeSong(songId: Long) {
        viewModelScope.launch {
            try {
                songRepository.likeSong(songId)
            } catch (e: Exception) {
                _artistDetailState.value = _artistDetailState.value.copy(
                    error = e.message ?: "Failed to like song"
                )
            }
        }
    }

    fun unlikeSong(songId: Long) {
        viewModelScope.launch {
            try {
                songRepository.unlikeSong(songId)
            } catch (e: Exception) {
                _artistDetailState.value = _artistDetailState.value.copy(
                    error = e.message ?: "Failed to unlike song"
                )
            }
        }
    }

    fun refresh() {
        loadArtistDetails()
    }

    fun clearError() {
        _artistDetailState.value = _artistDetailState.value.copy(error = null)
    }
}

data class ArtistDetailState(
    val isLoading: Boolean = false,
    val artist: Artist? = null,
    val albums: List<Album> = emptyList(),
    val topTracks: List<Song> = emptyList(),
    val error: String? = null
)