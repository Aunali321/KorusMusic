package li.auna.korusmusic.ui.screens.artistdetail

import androidx.lifecycle.SavedStateHandle
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
    savedStateHandle: SavedStateHandle,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val currentArtistId: Long = savedStateHandle.get<Long>("artistId") ?: 0L

    private val _artistState = MutableStateFlow(ArtistDetailState())
    val artistState: StateFlow<ArtistDetailState> = _artistState.asStateFlow()

    init {
        if (currentArtistId > 0) {
            loadArtistDetails()
        }
    }

    private fun loadArtistDetails() {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(isLoading = true)
            
            try {
                val artist = artistRepository.getArtist(currentArtistId)
                
                if (artist != null) {
                    // Collect data from multiple repositories
                    combine(
                        albumRepository.getAlbumsByArtist(currentArtistId),
                        songRepository.getSongsByArtist(currentArtistId)
                    ) { albums, songs ->
                        ArtistDetailState(
                            isLoading = false,
                            artist = artist,
                            albums = albums,
                            topTracks = songs.sortedByDescending { it.playCount }.take(10)
                        )
                    }.collect { newState ->
                        _artistState.value = newState
                    }
                } else {
                    _artistState.value = _artistState.value.copy(
                        isLoading = false,
                        error = "Artist not found"
                    )
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load artist details"
                )
            }
        }
    }

    fun playTopTracks() {
        val topTracks = _artistState.value.topTracks
        if (topTracks.isNotEmpty()) {
            playerManager.setQueue(topTracks, 0)
            playerManager.play()
        }
    }

    fun shuffleAllSongs() {
        viewModelScope.launch {
            try {
                val allSongs = songRepository.getSongsByArtist(currentArtistId)
                allSongs.collect { songs ->
                    if (songs.isNotEmpty()) {
                        playerManager.setQueue(songs.shuffled(), 0)
                        playerManager.play()
                    }
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    error = e.message ?: "Failed to play artist songs"
                )
            }
        }
    }

    fun playSong(song: Song) {
        val topTracks = _artistState.value.topTracks
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
                _artistState.value = _artistState.value.copy(
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
                artistRepository.followArtist(currentArtistId)
                // The artist state will be updated through the repository flow
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    error = e.message ?: "Failed to follow artist"
                )
            }
        }
    }

    fun unfollowArtist() {
        viewModelScope.launch {
            try {
                artistRepository.unfollowArtist(currentArtistId)
                // The artist state will be updated through the repository flow
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
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
                _artistState.value = _artistState.value.copy(
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
                _artistState.value = _artistState.value.copy(
                    error = e.message ?: "Failed to unlike song"
                )
            }
        }
    }

    fun refresh() {
        loadArtistDetails()
    }

    fun clearError() {
        _artistState.value = _artistState.value.copy(error = null)
    }
}

data class ArtistDetailState(
    val isLoading: Boolean = false,
    val artist: Artist? = null,
    val albums: List<Album> = emptyList(),
    val topTracks: List<Song> = emptyList(),
    val error: String? = null
)