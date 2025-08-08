package li.auna.korusmusic.ui.screens.albumdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Album
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.AlbumRepository
import li.auna.korusmusic.domain.repository.SongRepository
import li.auna.korusmusic.player.PlayerManager

class AlbumDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val currentAlbumId: Long = savedStateHandle.get<Long>("albumId") ?: 0L

    private val _albumState = MutableStateFlow(AlbumDetailState())
    val albumState: StateFlow<AlbumDetailState> = _albumState.asStateFlow()

    init {
        if (currentAlbumId > 0) {
            loadAlbumDetails()
        }
    }

    private fun loadAlbumDetails() {
        viewModelScope.launch {
            _albumState.value = _albumState.value.copy(isLoading = true)
            
            try {
                val album = albumRepository.getAlbum(currentAlbumId)
                val songs = songRepository.getSongsByAlbum(currentAlbumId)
                
                if (album != null) {
                    // Collect songs as flow to get real-time updates
                    songs.collect { songList ->
                        _albumState.value = _albumState.value.copy(
                            isLoading = false,
                            album = album,
                            songs = songList
                        )
                    }
                } else {
                    _albumState.value = _albumState.value.copy(
                        isLoading = false,
                        error = "Album not found"
                    )
                }
            } catch (e: Exception) {
                _albumState.value = _albumState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load album details"
                )
            }
        }
    }

    fun playAlbum() {
        val songs = _albumState.value.songs
        if (songs.isNotEmpty()) {
            playerManager.setQueue(songs, 0)
            playerManager.play()
        }
    }

    fun shuffleAlbum() {
        val songs = _albumState.value.songs
        if (songs.isNotEmpty()) {
            playerManager.setQueue(songs.shuffled(), 0)
            playerManager.play()
        }
    }

    fun playSong(song: Song) {
        val songs = _albumState.value.songs
        val index = songs.indexOf(song)
        if (index >= 0) {
            playerManager.setQueue(songs, index)
            playerManager.play()
        }
    }

    fun addSongToQueue(song: Song) {
        playerManager.addToQueue(song)
    }

    fun likeAlbum() {
        viewModelScope.launch {
            try {
                albumRepository.likeAlbum(currentAlbumId)
                // The album state will be updated through the repository flow
            } catch (e: Exception) {
                _albumState.value = _albumState.value.copy(
                    error = e.message ?: "Failed to like album"
                )
            }
        }
    }

    fun unlikeAlbum() {
        viewModelScope.launch {
            try {
                albumRepository.unlikeAlbum(currentAlbumId)
                // The album state will be updated through the repository flow
            } catch (e: Exception) {
                _albumState.value = _albumState.value.copy(
                    error = e.message ?: "Failed to unlike album"
                )
            }
        }
    }

    fun likeSong(songId: Long) {
        viewModelScope.launch {
            try {
                songRepository.likeSong(songId)
            } catch (e: Exception) {
                _albumState.value = _albumState.value.copy(
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
                _albumState.value = _albumState.value.copy(
                    error = e.message ?: "Failed to unlike song"
                )
            }
        }
    }

    fun refresh() {
        loadAlbumDetails()
    }

    fun clearError() {
        _albumState.value = _albumState.value.copy(error = null)
    }
}

data class AlbumDetailState(
    val isLoading: Boolean = false,
    val album: Album? = null,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)