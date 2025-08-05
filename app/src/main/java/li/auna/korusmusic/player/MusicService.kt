package li.auna.korusmusic.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import li.auna.korusmusic.MainActivity
import li.auna.korusmusic.R

class MusicService : LifecycleService() {

    private val playerManager: PlayerManager by inject()
    private val binder = MusicBinder()
    
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "korus_playback_channel"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS" 
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        playerManager.initialize()
        
        lifecycleScope.launch {
            playerManager.playerState.collect { state ->
                updateNotification(state)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleAction(intent?.action)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onDestroy() {
        playerManager.release()
        super.onDestroy()
    }

    private fun handleAction(action: String?) {
        when (action) {
            ACTION_PLAY_PAUSE -> playerManager.togglePlayPause()
            ACTION_PREVIOUS -> playerManager.seekToPrevious()
            ACTION_NEXT -> playerManager.seekToNext()
            ACTION_STOP -> {
                playerManager.stop()
                stopSelf()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for music playback"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(state: PlayerState) {
        val notification = createNotification(state)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(state: PlayerState): Notification {
        val currentSong = state.currentSong
        
        // Intent to open the app
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Control actions
        val playPauseIntent = PendingIntent.getService(
            this, 0,
            Intent(this, MusicService::class.java).setAction(ACTION_PLAY_PAUSE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val previousIntent = PendingIntent.getService(
            this, 0,
            Intent(this, MusicService::class.java).setAction(ACTION_PREVIOUS),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getService(
            this, 0,
            Intent(this, MusicService::class.java).setAction(ACTION_NEXT),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, MusicService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentSong?.title ?: "No song playing")
            .setContentText(currentSong?.artist?.name ?: "Unknown artist")
            .setSubText(currentSong?.album?.name)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // You'll need to add proper icons
            .setContentIntent(contentIntent)
            .setDeleteIntent(stopIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                android.R.drawable.ic_media_previous,
                "Previous",
                previousIntent
            )
            .addAction(
                if (state.isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (state.isPlaying) "Pause" else "Play",
                playPauseIntent
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "Next",
                nextIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .build()
    }

    inner class MusicBinder : Binder() {
        val playerManager: PlayerManager get() = this@MusicService.playerManager
    }
}