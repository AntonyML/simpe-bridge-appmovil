package com.simpe.bridge.appmovil.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simpe.bridge.appmovil.data.auth.SessionManager
import com.simpe.bridge.appmovil.data.remote.auth.SupabaseAuthDataSource
import com.simpe.bridge.appmovil.data.remote.auth.SupabaseProfileDataSource
import com.simpe.bridge.appmovil.data.repository.AuthRepositoryImpl
import com.simpe.bridge.appmovil.domain.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String?     = null,
    val success: Boolean   = false
)

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun login(phone: String, password: String) {
        if (phone.isBlank() || password.isBlank()) {
            _state.value = LoginUiState(error = "Ingresá tu teléfono y contraseña")
            return
        }
        viewModelScope.launch {
            Log.d(TAG, "Iniciando intento de login para: $phone")
            _state.value = LoginUiState(isLoading = true)
            loginUseCase(phone.trim(), password).fold(
                onSuccess = { 
                    Log.i(TAG, "Login exitoso para: $phone")
                    _state.value = LoginUiState(success = true) 
                },
                onFailure = { 
                    val errorMsg = it.message ?: ""
                    Log.e(TAG, "Error de login para $phone: $errorMsg", it)
                    
                    val userFriendlyError = when {
                        errorMsg.contains("invalid_credentials", ignoreCase = true) -> 
                            "Teléfono o contraseña incorrectos"
                        errorMsg.contains("network", ignoreCase = true) || 
                        errorMsg.contains("Permission denied", ignoreCase = true) ||
                        errorMsg.contains("Failed to connect", ignoreCase = true) ->
                            "Error de conexión. Revisa tu internet."
                        else -> "Ocurrió un error inesperado. Inténtalo de nuevo."
                    }
                    _state.value = LoginUiState(error = userFriendlyError)
                }
            )
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"

        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    // Manual dependency wiring (no DI framework).
                    // The ViewModel only knows about the UseCase.
                    // All data-layer classes are wired here and stay invisible to the UI.
                    val sessionManager    = SessionManager(context.applicationContext)
                    val authDataSource    = SupabaseAuthDataSource()
                    val profileDataSource = SupabaseProfileDataSource()
                    val repo              = AuthRepositoryImpl(
                        authDataSource    = authDataSource,
                        profileDataSource = profileDataSource,
                        sessionManager    = sessionManager
                    )
                    @Suppress("UNCHECKED_CAST")
                    return LoginViewModel(LoginUseCase(repo)) as T
                }
            }
    }
}
