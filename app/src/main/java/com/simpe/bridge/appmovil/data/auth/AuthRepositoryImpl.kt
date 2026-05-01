package com.simpe.bridge.appmovil.data.auth

import com.simpe.bridge.appmovil.domain.auth.AuthRepository

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(phone: String, password: String): Result<Unit> =
        authService.login(phone, password).map { session ->
            sessionManager.saveSession(session.accessToken, session.refreshToken)
        }

    override fun isLoggedIn() = sessionManager.isLoggedIn()
    override fun logout()     = sessionManager.clearSession()
}
