package li.auna.korusmusic.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import li.auna.korusmusic.data.database.KorusDatabase
import li.auna.korusmusic.data.auth.TokenManager
import li.auna.korusmusic.data.preferences.PreferencesManager
import li.auna.korusmusic.data.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "korus_preferences")

val databaseModule = module {
    
    single { KorusDatabase.buildDatabase(androidContext()) }
    
    single { get<KorusDatabase>().songDao() }
    single { get<KorusDatabase>().albumDao() }
    single { get<KorusDatabase>().artistDao() }
    single { get<KorusDatabase>().playlistDao() }
    single { get<KorusDatabase>().playHistoryDao() }
    
    single { androidContext().dataStore }
    
    single { TokenManager(get()) }
    
    single { PreferencesManager(get()) }
    
    single { CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>()) }
    
    single { DataManager(get(), get(), get(), get(), get()) }
}