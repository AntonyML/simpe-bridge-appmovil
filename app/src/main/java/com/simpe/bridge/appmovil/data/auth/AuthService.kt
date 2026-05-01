package com.simpe.bridge.appmovil.data.auth

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AuthSession(val accessToken: String, val refreshToken: String)

class AuthService {

    /**
     * El número de teléfono se usa como identidad en formato email:
     * "88887777" → "88887777@simpe.bridge"
     * Supabase maneja el hash de la contraseña internamente.
     */
    suspend fun login(phone: String, password: String): Result<AuthSession> =
        withContext(Dispatchers.IO) {
            runCatching {
                supabaseClient.auth.signInWith(Email) {
                    email = "$phone@simpe.bridge"
                    this.password = password
                }
                val session = supabaseClient.auth.currentSessionOrNull()
                    ?: throw Exception("No se pudo obtener la sesión de Supabase")
                AuthSession(
                    accessToken  = session.accessToken,
                    refreshToken = session.refreshToken ?: ""
                )
            }
        }
}
