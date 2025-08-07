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
import li.auna.korusmusic.domain.repository.AlbumRepository
import li.auna.korusmusic.domain.repository.ArtistRepository
import li.auna.korusmusic.domain.repository.PlaylistRepository
import li.auna.korusmusic.domain.repository.SongRepository
import li.auna.korusmusic.data.DataManager

class LibraryViewModel(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository,
    private val dataManager: DataManager
) : ViewModel() {

    private val _libraryState = MutableStateFlow(LibraryState())
    val libraryState: StateFlow<LibraryState> = _libraryState.asStateFlow()

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        viewModelScope.launch {
            try {
                // Combine data from repositories and sync status
                combine(
                    songRepository.getAllSongs(),
                    albumRepository.getAllAlbums(),
                    artistRepository.getAllArtists(),
                    playlistRepository.getAllPlaylists(),
                    dataManager.isSyncing
                ) { songs, albums, artists, playlists, isSyncing ->
                    LibraryState(
                        isLoading = isSyncing,
                        songs = songs,
                        albums = albums,
                        artists = artists,
                        playlists = playlists
                    )
                }.collect { newState ->
                    _libraryState.value = newState
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
        dataManager.refresh()
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