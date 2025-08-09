package li.auna.korusmusic.ui.screens.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Lyrics
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.LyricsRepository
import li.auna.korusmusic.domain.repository.SongRepository
import li.auna.korusmusic.player.PlayerManager
import li.auna.korusmusic.player.PlayerState
import li.auna.korusmusic.player.RepeatMode

class NowPlayingViewModel(
    private val playerManager: PlayerManager,
    private val songRepository: SongRepository,
    private val lyricsRepository: LyricsRepository
) : ViewModel() {

    private val _nowPlayingState = MutableStateFlow(NowPlayingScreenState())
    val nowPlayingState: StateFlow<NowPlayingScreenState> = _nowPlayingState.asStateFlow()
    
    private val _lyricsState = MutableStateFlow<List<Lyrics>>(emptyList())
    val lyricsState: StateFlow<List<Lyrics>> = _lyricsState.asStateFlow()
    
    private val _selectedLanguage = MutableStateFlow("eng")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        // Observe player state changes
        viewModelScope.launch {
            playerManager.playerState.collect { playerState ->
                _nowPlayingState.value = NowPlayingScreenState(
                    playerState = playerState,
                    currentSong = playerState.currentSong,
                    queue = playerState.queue,
                    isShuffleEnabled = playerState.shuffleMode,
                    repeatMode = when (playerState.repeatMode) {
                        RepeatMode.OFF -> RepeatMode.OFF
                        RepeatMode.ONE -> RepeatMode.ONE
                        RepeatMode.ALL -> RepeatMode.ALL
                    }
                )
                
                // Load lyrics when song changes
                val currentSongId = playerState.currentSong?.id
                if (currentSongId != null) {
                    loadLyricsForSong(currentSongId)
                } else {
                    _lyricsState.value = emptyList()
                }
            }
        }
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun skipToNext() {
        playerManager.seekToNext()
    }

    fun skipToPrevious() {
        playerManager.seekToPrevious()
    }

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }

    fun toggleShuffle() {
        val currentState = _nowPlayingState.value
        playerManager.setShuffleMode(!currentState.isShuffleEnabled)
    }

    fun toggleRepeat() {
        val currentMode = _nowPlayingState.value.repeatMode
        val newMode = when (currentMode) {
            RepeatMode.OFF -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.OFF
        }
        playerManager.setRepeatMode(newMode)
    }

    fun skipToTrack(index: Int) {
        playerManager.seekToIndex(index)
    }

    fun removeFromQueue(index: Int) {
        playerManager.removeFromQueue(index)
    }

    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        // This functionality will need to be implemented in PlayerManager
        // For now, just remove and add back at new position
    }

    fun likeSong(songId: Long) {
        viewModelScope.launch {
            try {
                songRepository.likeSong(songId)
            } catch (e: Exception) {
                // Handle error - could show toast
            }
        }
    }

    fun unlikeSong(songId: Long) {
        viewModelScope.launch {
            try {
                songRepository.unlikeSong(songId)
            } catch (e: Exception) {
                // Handle error - could show toast
            }
        }
    }

    fun addToPlaylist(songId: Long, playlistId: Long) {
        viewModelScope.launch {
            try {
                // This would require PlaylistRepository
                // playlistRepository.addSongsToPlaylist(playlistId, listOf(songId))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun setSelectedLanguage(language: String) {
        _selectedLanguage.value = language
    }
    
    private fun loadLyricsForSong(songId: Long) {
        viewModelScope.launch {
            try {
                lyricsRepository.getLyricsBySongId(songId).collect { lyrics ->
                    _lyricsState.value = lyrics
                }
            } catch (e: Exception) {
                _lyricsState.value = emptyList()
            }
        }
    }
    
    fun refreshLyrics(songId: Long) {
        viewModelScope.launch {
            try {
                lyricsRepository.syncLyricsForSong(songId)
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
}

data class NowPlayingScreenState(
    val playerState: PlayerState = PlayerState(),
    val currentSong: Song? = null,
    val queue: List<Song> = emptyList(),
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)

enum class RepeatMode {
    OFF, ONE, ALL
}