package li.auna.korusmusic.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Album
import li.auna.korusmusic.domain.model.Artist
import li.auna.korusmusic.domain.model.Playlist
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.AlbumRepository
import li.auna.korusmusic.domain.repository.ArtistRepository
import li.auna.korusmusic.domain.repository.PlaylistRepository
import li.auna.korusmusic.domain.repository.SongRepository

class SearchViewModel(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState()
            return
        }

        viewModelScope.launch {
            _searchState.value = _searchState.value.copy(
                isLoading = true,
                query = query,
                error = null
            )

            try {
                // Search across all repositories in parallel
                val songs = songRepository.searchSongs(query)
                val albums = albumRepository.searchAlbums(query)
                val artists = artistRepository.searchArtists(query)
                val playlists = playlistRepository.searchPlaylists(query)

                _searchState.value = _searchState.value.copy(
                    isLoading = false,
                    searchResults = SearchResults(
                        songs = songs,
                        albums = albums,
                        artists = artists,
                        playlists = playlists
                    )
                )
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Search failed"
                )
            }
        }
    }

    fun clearSearch() {
        _searchState.value = SearchState()
    }

    fun clearError() {
        _searchState.value = _searchState.value.copy(error = null)
    }
}

data class SearchState(
    val isLoading: Boolean = false,
    val query: String = "",
    val searchResults: SearchResults = SearchResults(),
    val error: String? = null
)

data class SearchResults(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<Playlist> = emptyList()
) {
    val isEmpty: Boolean
        get() = songs.isEmpty() && albums.isEmpty() && artists.isEmpty() && playlists.isEmpty()

    val hasResults: Boolean
        get() = !isEmpty
}