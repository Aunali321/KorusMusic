package li.auna.korusmusic.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.AlbumRepository
import li.auna.korusmusic.domain.repository.ArtistRepository
import li.auna.korusmusic.domain.repository.PlaylistRepository
import li.auna.korusmusic.domain.repository.SongRepository
import li.auna.korusmusic.data.DataManager

class HomeViewModel(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository,
    private val dataManager: DataManager
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                // Combine data from repositories and sync status
                combine(
                    songRepository.getRecentlyPlayedSongs(10),
                    albumRepository.getRecentlyAddedAlbums(10),
                    songRepository.getAllSongs(),
                    dataManager.isSyncing
                ) { recentlyPlayed, recentAlbums, allSongs, isSyncing ->
                    HomeState(
                        isLoading = isSyncing,
                        homeData = HomeData(
                            recentlyPlayed = recentlyPlayed,
                            recentlyAdded = allSongs.sortedByDescending { it.dateAdded }.take(10),
                            recommendedAlbums = recentAlbums
                        )
                    )
                }.collect { newState ->
                    _homeState.value = newState
                }
            } catch (e: Exception) {
                _homeState.value = _homeState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load home data"
                )
            }
        }
    }

    fun refresh() {
        dataManager.refresh()
    }

    fun clearError() {
        _homeState.value = _homeState.value.copy(error = null)
    }
}

data class HomeState(
    val isLoading: Boolean = false,
    val homeData: HomeData? = null,
    val error: String? = null
)

data class HomeData(
    val recentlyPlayed: List<Song> = emptyList(),
    val recentlyAdded: List<Song> = emptyList(),
    val recommendedSongs: List<Song> = emptyList(),
    val recommendedAlbums: List<li.auna.korusmusic.domain.model.Album> = emptyList()
)