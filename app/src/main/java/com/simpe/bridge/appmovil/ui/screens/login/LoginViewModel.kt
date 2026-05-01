package com.simpe.bridge.appmovil.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simpe.bridge.appmovil.data.auth.AuthRepositoryImpl
import com.simpe.bridge.appmovil.data.auth.AuthService
import com.simpe.bridge.appmovil.data.auth.SessionManager
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
            _state.value = LoginUiState(isLoading = true)
            loginUseCase(phone.trim(), password).fold(
                onSuccess = { _state.value = LoginUiState(success = true) },
                onFailure = { _state.value = LoginUiState(error = it.message ?: "Error al iniciar sesión") }
            )
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val sessionManager = SessionManager(context.applicationContext)
                    val authService    = AuthService()
                    val repo           = AuthRepositoryImpl(authService, sessionManager)
                    @Suppress("UNCHECKED_CAST")
                    return LoginViewModel(LoginUseCase(repo)) as T
                }
            }
    }
}
