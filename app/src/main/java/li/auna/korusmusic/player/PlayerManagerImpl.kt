package li.auna.korusmusic.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.Song
import li.auna.korusmusic.domain.repository.SongRepository
import li.auna.korusmusic.data.preferences.PreferencesManager
import kotlinx.coroutines.runBlocking
import android.util.Log

class PlayerManagerImpl(
    private val exoPlayer: ExoPlayer,
    private val songRepository: SongRepository,
    private val preferencesManager: PreferencesManager
) : PlayerManager, Player.Listener {

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val coroutineScope = CoroutineScope(SupervisorJob())
    private var positionUpdateJob: kotlinx.coroutines.Job? = null

    override fun initialize() {
        exoPlayer.addListener(this)
        startPositionUpdates()
    }

    override fun release() {
        positionUpdateJob?.cancel()
        exoPlayer.removeListener(this)
        exoPlayer.release()
    }

    override fun play() {
        Log.d("PlayerManager", "play() called - current state: isPlaying=${exoPlayer.isPlaying}, playWhenReady=${exoPlayer.playWhenReady}")
        exoPlayer.play()
        Log.d("PlayerManager", "play() after - isPlaying=${exoPlayer.isPlaying}, playWhenReady=${exoPlayer.playWhenReady}")
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            currentPosition = 0L
        )
    }

    override fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    override fun setQueue(songs: List<Song>, startIndex: Int) {
        val mediaItems = songs.map { song ->
            createMediaItem(song)
        }
        
        Log.d("PlayerManager", "setQueue: ${songs.size} songs, startIndex: $startIndex")
        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()
        
        _playerState.value = _playerState.value.copy(
            queue = songs,
            currentIndex = startIndex,
            currentSong = songs.getOrNull(startIndex)
        )
        
        Log.d("PlayerManager", "Queue set, auto-playing...")
        exoPlayer.play()
    }

    override fun addToQueue(song: Song) {
        addToQueue(listOf(song))
    }

    override fun addToQueue(songs: List<Song>) {
        val mediaItems = songs.map { createMediaItem(it) }
        mediaItems.forEach { exoPlayer.addMediaItem(it) }
        
        val currentQueue = _playerState.value.queue.toMutableList()
        currentQueue.addAll(songs)
        
        _playerState.value = _playerState.value.copy(queue = currentQueue)
    }

    override fun removeFromQueue(index: Int) {
        if (index in _playerState.value.queue.indices) {
            exoPlayer.removeMediaItem(index)
            val currentQueue = _playerState.value.queue.toMutableList()
            currentQueue.removeAt(index)
            
            val newCurrentIndex = when {
                index < _playerState.value.currentIndex -> _playerState.value.currentIndex - 1
                index == _playerState.value.currentIndex -> _playerState.value.currentIndex
                else -> _playerState.value.currentIndex
            }
            
            _playerState.value = _playerState.value.copy(
                queue = currentQueue,
                currentIndex = newCurrentIndex,
                currentSong = currentQueue.getOrNull(newCurrentIndex)
            )
        }
    }

    override fun clearQueue() {
        exoPlayer.clearMediaItems()
        _playerState.value = _playerState.value.copy(
            queue = emptyList(),
            currentIndex = -1,
            currentSong = null
        )
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    override fun seekToNext() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNext()
        }
    }

    override fun seekToPrevious() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPrevious()
        }
    }

    override fun seekToIndex(index: Int) {
        if (index in _playerState.value.queue.indices) {
            exoPlayer.seekTo(index, 0L)
        }
    }

    override fun setRepeatMode(repeatMode: RepeatMode) {
        val exoRepeatMode = when (repeatMode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
        exoPlayer.repeatMode = exoRepeatMode
        _playerState.value = _playerState.value.copy(repeatMode = repeatMode)
    }

    override fun setShuffleMode(enabled: Boolean) {
        exoPlayer.shuffleModeEnabled = enabled
        _playerState.value = _playerState.value.copy(shuffleMode = enabled)
    }

    override fun setPlaybackSpeed(speed: Float) {
        exoPlayer.setPlaybackSpeed(speed)
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
    }

    override fun getCurrentSong(): Song? = _playerState.value.currentSong

    override fun getCurrentPosition(): Long = exoPlayer.currentPosition

    override fun getDuration(): Long = exoPlayer.duration

    // Player.Listener implementations
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d("PlayerManager", "onIsPlayingChanged: $isPlaying")
        _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
        
        if (isPlaying) {
            // Record play for statistics
            _playerState.value.currentSong?.let { song ->
                coroutineScope.launch {
                    songRepository.recordPlay(song.id, System.currentTimeMillis().toString())
                }
            }
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        val currentIndex = exoPlayer.currentMediaItemIndex
        val currentSong = _playerState.value.queue.getOrNull(currentIndex)
        
        _playerState.value = _playerState.value.copy(
            currentIndex = currentIndex,
            currentSong = currentSong,
            duration = exoPlayer.duration
        )
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e("PlayerManager", "Player error: ${error.message}", error)
        _playerState.value = _playerState.value.copy(
            error = error.message,
            isLoading = false
        )
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        _playerState.value = _playerState.value.copy(isLoading = isLoading)
    }

    private fun createMediaItem(song: Song): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist.name)
            .setAlbumTitle(song.album.name)
            .build()

        val serverUrl = runBlocking { preferencesManager.getServerUrl() }
        return MediaItem.Builder()
            .setUri(song.getStreamUrl(serverUrl))
            .setMediaId(song.id.toString())
            .setMediaMetadata(metadata)
            .build()
    }

    private fun startPositionUpdates() {
        positionUpdateJob = coroutineScope.launch(Dispatchers.Main) {
            while (true) {
                if (exoPlayer.isPlaying) {
                    _playerState.value = _playerState.value.copy(
                        currentPosition = exoPlayer.currentPosition,
                        duration = exoPlayer.duration
                    )
                }
                delay(100) // Update every 100ms
            }
        }
    }
}