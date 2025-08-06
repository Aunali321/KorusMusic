package li.auna.korusmusic.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import li.auna.korusmusic.BuildConfig

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SERVER_URL_KEY = stringPreferencesKey("server_url")
        private val IS_DARK_THEME_KEY = booleanPreferencesKey("is_dark_theme")
        private val STREAMING_QUALITY_KEY = stringPreferencesKey("streaming_quality")
        private val DOWNLOAD_QUALITY_KEY = stringPreferencesKey("download_quality")
        private val AUTO_DOWNLOAD_ON_WIFI_KEY = booleanPreferencesKey("auto_download_on_wifi")
    }

    val serverUrl: Flow<String> = dataStore.data.map { preferences ->
        preferences[SERVER_URL_KEY] ?: getDefaultServerUrl()
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_DARK_THEME_KEY] ?: false
    }

    val streamingQuality: Flow<String> = dataStore.data.map { preferences ->
        preferences[STREAMING_QUALITY_KEY] ?: "HIGH"
    }

    val downloadQuality: Flow<String> = dataStore.data.map { preferences ->
        preferences[DOWNLOAD_QUALITY_KEY] ?: "HIGH"
    }

    val autoDownloadOnWiFi: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTO_DOWNLOAD_ON_WIFI_KEY] ?: true
    }

    suspend fun setServerUrl(url: String) {
        val normalizedUrl = normalizeServerUrl(url)
        dataStore.edit { preferences ->
            preferences[SERVER_URL_KEY] = normalizedUrl
        }
    }

    suspend fun getServerUrl(): String {
        return serverUrl.first()
    }

    suspend fun resetServerUrl() {
        dataStore.edit { preferences ->
            preferences.remove(SERVER_URL_KEY)
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_THEME_KEY] = isDark
        }
    }

    suspend fun setStreamingQuality(quality: String) {
        dataStore.edit { preferences ->
            preferences[STREAMING_QUALITY_KEY] = quality
        }
    }

    suspend fun setDownloadQuality(quality: String) {
        dataStore.edit { preferences ->
            preferences[DOWNLOAD_QUALITY_KEY] = quality
        }
    }

    suspend fun setAutoDownloadOnWiFi(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_DOWNLOAD_ON_WIFI_KEY] = enabled
        }
    }

    private fun getDefaultServerUrl(): String {
        return ""
    }

    private fun normalizeServerUrl(url: String): String {
        var normalizedUrl = url.trim()
        
        // Add protocol if missing
        if (!normalizedUrl.startsWith("http://") && !normalizedUrl.startsWith("https://")) {
            normalizedUrl = "https://$normalizedUrl"
        }
        
        // Ensure it ends with trailing slash
        if (!normalizedUrl.endsWith("/")) {
            normalizedUrl += "/"
        }
        
        return normalizedUrl
    }

    fun isCustomServerUrl(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SERVER_URL_KEY] != null
    }
} 