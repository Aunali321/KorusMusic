package li.auna.korusmusic.data.network

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import li.auna.korusmusic.data.preferences.PreferencesManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class KorusApiServiceProvider(
    private val httpClient: OkHttpClient,
    private val json: Json,
    private val preferencesManager: PreferencesManager
) {
    private var currentServerUrl: String? = null
    private var apiService: KorusApiService? = null
    
    fun getApiService(): KorusApiService {
        val serverUrl = runBlocking { 
            val url = preferencesManager.getServerUrl()
            if (url.isBlank()) "http://localhost:8080/" else url
        }
        
        // Recreate API service if URL changed
        if (currentServerUrl != serverUrl || apiService == null) {
            currentServerUrl = serverUrl
            apiService = createApiService(serverUrl)
        }
        
        return apiService!!
    }
    
    private fun createApiService(baseUrl: String): KorusApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(KorusApiService::class.java)
    }
}