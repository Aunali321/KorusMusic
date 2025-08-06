package li.auna.korusmusic.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import li.auna.korusmusic.domain.repository.AuthRepository
import li.auna.korusmusic.data.preferences.PreferencesManager
import li.auna.korusmusic.data.DataManager

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager,
    private val dataManager: DataManager
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    init {
        loadServerUrl()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)
            
            try {
                val user = authRepository.login(username, password)
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    user = user
                )
                // Trigger initial data sync after successful login
                dataManager.performInitialSync()
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }

    fun clearError() {
        _loginState.value = _loginState.value.copy(error = null)
    }
    
    private fun loadServerUrl() {
        viewModelScope.launch {
            preferencesManager.serverUrl.collect { url ->
                _serverUrl.value = url
            }
        }
    }
    
    fun setServerUrl(url: String) {
        viewModelScope.launch {
            try {
                preferencesManager.setServerUrl(url)
                _loginState.value = _loginState.value.copy(error = null)
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    error = "Failed to set server URL: ${e.message}"
                )
            }
        }
    }
    
    fun login(username: String, password: String, serverUrl: String) {
        viewModelScope.launch {
            // Set server URL first
            setServerUrl(serverUrl)
            
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)
            
            try {
                val user = authRepository.login(username, password)
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    user = user
                )
                // Trigger initial data sync after successful login
                dataManager.performInitialSync()
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }
}

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: li.auna.korusmusic.domain.model.User? = null
)