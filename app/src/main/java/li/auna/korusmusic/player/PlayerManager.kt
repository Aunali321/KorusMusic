package li.auna.korusmusic.player

import kotlinx.coroutines.flow.StateFlow
import li.auna.korusmusic.domain.model.Song

interface PlayerManager {
    val playerState: StateFlow<PlayerState>
    
    fun initialize()
    fun release()
    
    // Playback control
    fun play()
    fun pause()
    fun stop()
    fun togglePlayPause()
    
    // Queue management
    fun setQueue(songs: List<Song>, startIndex: Int = 0)
    fun addToQueue(song: Song)
    fun addToQueue(songs: List<Song>)
    fun removeFromQueue(index: Int)
    fun clearQueue()
    
    // Navigation
    fun seekTo(position: Long)
    fun seekToNext()
    fun seekToPrevious()
    fun seekToIndex(index: Int)
    
    // Playback settings
    fun setRepeatMode(repeatMode: RepeatMode)
    fun setShuffleMode(enabled: Boolean)
    fun setPlaybackSpeed(speed: Float)
    
    // Current song
    fun getCurrentSong(): Song?
    fun getCurrentPosition(): Long
    fun getDuration(): Long
}