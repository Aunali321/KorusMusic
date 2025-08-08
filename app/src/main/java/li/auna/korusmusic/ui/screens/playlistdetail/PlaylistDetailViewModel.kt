package li.auna.korusmusic.ui.screens.playlistdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Playlist
import li.auna.korusmusic.domain.model.PlaylistSong
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.PlaylistRepository
import li.auna.korusmusic.domain.repository.SongRepository
import li.auna.korusmusic.player.PlayerManager

class PlaylistDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val currentPlaylistId: Long = savedStateHandle.get<Long>("playlistId") ?: 0L

    private val _playlistState = MutableStateFlow(PlaylistDetailState())
    val playlistState: StateFlow<PlaylistDetailState> = _playlistState.asStateFlow()

    init {
        if (currentPlaylistId > 0) {
            loadPlaylistDetails()
        }
    }

    private fun loadPlaylistDetails() {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true)
            
            try {
                val playlist = playlistRepository.getPlaylist(currentPlaylistId)
                
                if (playlist != null) {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        playlist = playlist,
                        playlistSongs = playlist.songs
                    )
                } else {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = "Playlist not found"
                    )
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load playlist details"
                )
            }
        }
    }

    fun playPlaylist() {
        val songs = _playlistState.value.playlistSongs.map { it.song }
        if (songs.isNotEmpty()) {
            playerManager.setQueue(songs, 0)
            playerManager.play()
        }
    }

    fun shufflePlaylist() {
        val songs = _playlistState.value.playlistSongs.map { it.song }.shuffled()
        if (songs.isNotEmpty()) {
            playerManager.setQueue(songs, 0)
            playerManager.play()
        }
    }

    fun playSong(playlistSong: PlaylistSong) {
        val songs = _playlistState.value.playlistSongs.map { it.song }
        val index = songs.indexOf(playlistSong.song)
        if (index >= 0) {
            playerManager.setQueue(songs, index)
            playerManager.play()
        }
    }

    fun addSongToQueue(song: Song) {
        playerManager.addToQueue(song)
    }

    fun addSongsToPlaylist(songIds: List<Long>) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true)
            
            try {
                playlistRepository.addSongsToPlaylist(currentPlaylistId, songIds)
                loadPlaylistDetails() // Refresh to get updated list
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add songs to playlist"
                )
            }
        }
    }

    fun removeSongsFromPlaylist(songIds: List<Long>) {
        viewModelScope.launch {
            try {
                playlistRepository.removeSongsFromPlaylist(currentPlaylistId, songIds)
                loadPlaylistDetails() // Refresh to get updated list
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    error = e.message ?: "Failed to remove songs from playlist"
                )
            }
        }
    }

    fun reorderPlaylistSong(songId: Long, newPosition: Int) {
        viewModelScope.launch {
            try {
                playlistRepository.reorderPlaylist(currentPlaylistId, songId, newPosition)
                loadPlaylistDetails() // Refresh to get updated order
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    error = e.message ?: "Failed to reorder playlist"
                )
            }
        }
    }

    fun updatePlaylistInfo(name: String, description: String?, isPublic: Boolean) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true)
            
            try {
                playlistRepository.updatePlaylist(currentPlaylistId, name, description, isPublic)
                loadPlaylistDetails() // Refresh to get updated info
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update playlist"
                )
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true)
            
            try {
                playlistRepository.deletePlaylist(currentPlaylistId)
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    isDeleted = true
                )
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete playlist"
                )
            }
        }
    }

    fun likeSong(songId: Long) {
        viewModelScope.launch {
            try {
                songRepository.likeSong(songId)
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
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
                _playlistState.value = _playlistState.value.copy(
                    error = e.message ?: "Failed to unlike song"
                )
            }
        }
    }

    fun refresh() {
        loadPlaylistDetails()
    }

    fun clearError() {
        _playlistState.value = _playlistState.value.copy(error = null)
    }

    // UI state management for editing mode
    fun toggleEditMode() {
        val currentMode = _playlistState.value.isEditMode
        _playlistState.value = _playlistState.value.copy(isEditMode = !currentMode)
    }

    fun selectSong(playlistSong: PlaylistSong) {
        val currentSelected = _playlistState.value.selectedSongs.toMutableSet()
        if (currentSelected.contains(playlistSong)) {
            currentSelected.remove(playlistSong)
        } else {
            currentSelected.add(playlistSong)
        }
        _playlistState.value = _playlistState.value.copy(selectedSongs = currentSelected)
    }

    fun clearSelection() {
        _playlistState.value = _playlistState.value.copy(selectedSongs = emptySet())
    }

    fun deleteSelectedSongs() {
        val selectedSongIds = _playlistState.value.selectedSongs.map { it.song.id }
        if (selectedSongIds.isNotEmpty()) {
            removeSongsFromPlaylist(selectedSongIds)
            clearSelection()
            toggleEditMode()
        }
    }
}

data class PlaylistDetailState(
    val isLoading: Boolean = false,
    val playlist: Playlist? = null,
    val playlistSongs: List<PlaylistSong> = emptyList(),
    val isDeleted: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val selectedSongs: Set<PlaylistSong> = emptySet()
)