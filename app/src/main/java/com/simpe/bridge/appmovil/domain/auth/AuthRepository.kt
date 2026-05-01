package com.simpe.bridge.appmovil.domain.auth

interface AuthRepository {
    suspend fun login(phone: String, password: String): Result<Unit>
    fun isLoggedIn(): Boolean
    fun logout()
}
