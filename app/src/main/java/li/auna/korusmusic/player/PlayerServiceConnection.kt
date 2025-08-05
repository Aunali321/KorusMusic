package li.auna.korusmusic.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import li.auna.korusmusic.domain.model.Song

class PlayerServiceConnection(
    private val context: Context
) {
    private var _playerManager = MutableStateFlow<PlayerManager?>(null)
    val playerManager: StateFlow<PlayerManager?> = _playerManager.asStateFlow()

    private var _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            _playerManager.value = binder.playerManager
            _isConnected.value = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            _playerManager.value = null
            _isConnected.value = false
        }
    }

    fun connect() {
        val intent = Intent(context, MusicService::class.java)
        context.startService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun disconnect() {
        if (_isConnected.value) {
            context.unbindService(serviceConnection)
            _isConnected.value = false
            _playerManager.value = null
        }
    }

    // Convenience methods that delegate to the player manager
    fun play() = _playerManager.value?.play()
    fun pause() = _playerManager.value?.pause()
    fun togglePlayPause() = _playerManager.value?.togglePlayPause()
    
    fun setQueue(songs: List<Song>, startIndex: Int = 0) {
        _playerManager.value?.setQueue(songs, startIndex)
    }
    
    fun seekTo(position: Long) = _playerManager.value?.seekTo(position)
    fun seekToNext() = _playerManager.value?.seekToNext()
    fun seekToPrevious() = _playerManager.value?.seekToPrevious()
    
    fun setRepeatMode(repeatMode: RepeatMode) {
        _playerManager.value?.setRepeatMode(repeatMode)
    }
    
    fun setShuffleMode(enabled: Boolean) {
        _playerManager.value?.setShuffleMode(enabled)
    }
}