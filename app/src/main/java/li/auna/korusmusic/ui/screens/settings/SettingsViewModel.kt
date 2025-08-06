package li.auna.korusmusic.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.model.User
import li.auna.korusmusic.domain.repository.AuthRepository
import li.auna.korusmusic.data.preferences.PreferencesManager

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()
    
    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    init {
        loadCurrentUser()
        loadServerUrl()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(isLoading = true)
            
            try {
                val user = authRepository.getCurrentUser()
                _settingsState.value = _settingsState.value.copy(
                    isLoading = false,
                    currentUser = user
                )
            } catch (e: Exception) {
                _settingsState.value = _settingsState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load user info"
                )
            }
        }
    }
    
    private fun loadServerUrl() {
        viewModelScope.launch {
            preferencesManager.serverUrl.collect { url ->
                _serverUrl.value = url
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(isLoading = true)
            
            try {
                authRepository.logout()
                _settingsState.value = _settingsState.value.copy(
                    isLoading = false,
                    isLoggedOut = true
                )
            } catch (e: Exception) {
                _settingsState.value = _settingsState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to logout"
                )
            }
        }
    }

    fun clearError() {
        _settingsState.value = _settingsState.value.copy(error = null)
    }

    fun refreshUserInfo() {
        loadCurrentUser()
    }

    fun updateThemePreference(isDarkMode: Boolean) {
        _settingsState.value = _settingsState.value.copy(isDarkTheme = isDarkMode)
        // In a real app, you might save this to SharedPreferences or DataStore
    }

    fun updateStreamingQuality(quality: StreamingQuality) {
        _settingsState.value = _settingsState.value.copy(streamingQuality = quality)
        // In a real app, you might save this to SharedPreferences or DataStore
    }

    fun updateDownloadQuality(quality: DownloadQuality) {
        _settingsState.value = _settingsState.value.copy(downloadQuality = quality)
        // In a real app, you might save this to SharedPreferences or DataStore
    }

    fun toggleAutoDownloadOnWiFi() {
        val current = _settingsState.value.autoDownloadOnWiFi
        _settingsState.value = _settingsState.value.copy(autoDownloadOnWiFi = !current)
        // In a real app, you might save this to SharedPreferences or DataStore
    }
    
    fun setServerUrl(url: String) {
        viewModelScope.launch {
            try {
                preferencesManager.setServerUrl(url)
                _settingsState.value = _settingsState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _settingsState.value = _settingsState.value.copy(
                    error = "Failed to set server URL: ${e.message}"
                )
            }
        }
    }
    
    fun resetServerUrl() {
        viewModelScope.launch {
            try {
                preferencesManager.resetServerUrl()
                _settingsState.value = _settingsState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _settingsState.value = _settingsState.value.copy(
                    error = "Failed to reset server URL: ${e.message}"
                )
            }
        }
    }
}

data class SettingsState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val isLoggedOut: Boolean = false,
    val error: String? = null,
    val isDarkTheme: Boolean = false,
    val streamingQuality: StreamingQuality = StreamingQuality.HIGH,
    val downloadQuality: DownloadQuality = DownloadQuality.HIGH,
    val autoDownloadOnWiFi: Boolean = true
)

enum class StreamingQuality {
    LOW, NORMAL, HIGH, LOSSLESS
}

enum class DownloadQuality {
    LOW, NORMAL, HIGH, LOSSLESS
}