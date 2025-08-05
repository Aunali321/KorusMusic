package li.auna.korusmusic.di

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import li.auna.korusmusic.player.PlayerManager
import li.auna.korusmusic.player.PlayerManagerImpl
import li.auna.korusmusic.player.PlayerServiceConnection
import java.io.File

val playerModule = module {
    
    // Database provider for ExoPlayer cache
    single<DatabaseProvider> { StandaloneDatabaseProvider(androidContext()) }
    
    // Cache for media files
    single<androidx.media3.datasource.cache.Cache> {
        val cacheDir = File(androidContext().cacheDir, "media_cache")
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024) // 512MB
        SimpleCache(cacheDir, cacheEvictor, get<DatabaseProvider>())
    }
    
    // Data source factory with caching
    single<DataSource.Factory> {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("KorusMusic/1.0")
            .setConnectTimeoutMs(30000)
            .setReadTimeoutMs(30000)
        
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(get())
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setCacheWriteDataSinkFactory(null) // Cache on read
        
        DefaultDataSource.Factory(androidContext(), cacheDataSourceFactory)
    }
    
    // ExoPlayer instance
    single {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        ExoPlayer.Builder(androidContext())
            .setMediaSourceFactory(DefaultMediaSourceFactory(get<DataSource.Factory>()))
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }
    
    // Player manager
    single<PlayerManager> { PlayerManagerImpl(get(), get()) }
    
    // Player service connection
    single { PlayerServiceConnection(androidContext()) }
}