package li.auna.korusmusic

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import li.auna.korusmusic.di.appModules

class KorusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@KorusApplication)
            modules(appModules)
        }
    }
}