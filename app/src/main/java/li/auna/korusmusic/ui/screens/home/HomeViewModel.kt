package li.auna.korusmusic.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.SongRepository

class HomeViewModel(
    private val songRepository: SongRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true)
            
            try {
                // First, sync data from API
                songRepository.syncSongs()
                
                // Then collect data from different sources
                combine(
                    songRepository.getRecentlyPlayedSongs(10),
                    songRepository.getAllSongs()
                ) { recentlyPlayed, allSongs ->
                    HomeData(
                        recentlyPlayed = recentlyPlayed,
                        recentlyAdded = allSongs.take(10) // Simple implementation
                    )
                }.collect { homeData ->
                    _homeState.value = _homeState.value.copy(
                        isLoading = false,
                        homeData = homeData
                    )
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
        viewModelScope.launch {
            try {
                songRepository.syncSongs()
                loadHomeData()
            } catch (e: Exception) {
                _homeState.value = _homeState.value.copy(
                    error = e.message ?: "Failed to refresh data"
                )
            }
        }
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