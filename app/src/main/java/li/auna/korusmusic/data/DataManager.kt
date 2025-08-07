package li.auna.korusmusic.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import li.auna.korusmusic.domain.repository.AlbumRepository
import li.auna.korusmusic.domain.repository.ArtistRepository
import li.auna.korusmusic.domain.repository.PlaylistRepository
import li.auna.korusmusic.domain.repository.SongRepository

class DataManager(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository,
    private val appScope: CoroutineScope
) {
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()
    
    fun performInitialSync() {
        if (_isSyncing.value) return // Already syncing
        
        appScope.launch {
            _isSyncing.value = true
            
            try {
                // Sync repositories sequentially to respect foreign key dependencies
                artistRepository.syncArtists()    // First - no dependencies
                albumRepository.syncAlbums()      // Second - depends on artists
                songRepository.syncSongs()        // Third - depends on artists and albums  
                playlistRepository.syncPlaylists() // Fourth - no dependencies but last
                
                _lastSyncTime.value = System.currentTimeMillis()
            } finally {
                _isSyncing.value = false
            }
        }
    }
    
    fun refresh() {
        appScope.launch {
            _isSyncing.value = true
            
            try {
                // Sync repositories sequentially to respect foreign key dependencies
                artistRepository.syncArtists()    // First - no dependencies
                albumRepository.syncAlbums()      // Second - depends on artists
                songRepository.syncSongs()        // Third - depends on artists and albums  
                playlistRepository.syncPlaylists() // Fourth - no dependencies but last
                
                _lastSyncTime.value = System.currentTimeMillis()
            } finally {
                _isSyncing.value = false
            }
        }
    }
}