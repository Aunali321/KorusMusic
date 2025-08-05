package li.auna.korusmusic.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Album
import li.auna.korusmusic.domain.model.Artist
import li.auna.korusmusic.domain.model.Playlist
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.SongRepository

class LibraryViewModel(
    private val songRepository: SongRepository
    // TODO: Add other repositories when implemented
    // private val albumRepository: AlbumRepository,
    // private val artistRepository: ArtistRepository,
    // private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _libraryState = MutableStateFlow(LibraryState())
    val libraryState: StateFlow<LibraryState> = _libraryState.asStateFlow()

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        viewModelScope.launch {
            _libraryState.value = _libraryState.value.copy(isLoading = true)
            
            try {
                // First, sync data from API
                songRepository.syncSongs()
                
                songRepository.getAllSongs().collect { songs ->
                    _libraryState.value = _libraryState.value.copy(
                        isLoading = false,
                        songs = songs,
                        // TODO: Load other data types when repositories are implemented
                        albums = emptyList(),
                        artists = emptyList(),
                        playlists = emptyList()
                    )
                }
            } catch (e: Exception) {
                _libraryState.value = _libraryState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load library data"
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                songRepository.syncSongs()
                loadLibraryData()
            } catch (e: Exception) {
                _libraryState.value = _libraryState.value.copy(
                    error = e.message ?: "Failed to refresh library"
                )
            }
        }
    }

    fun clearError() {
        _libraryState.value = _libraryState.value.copy(error = null)
    }
}

data class LibraryState(
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val error: String? = null
)