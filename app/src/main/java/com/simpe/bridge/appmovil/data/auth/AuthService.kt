package com.simpe.bridge.appmovil.data.auth

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

data class AuthSession(val accessToken: String, val refreshToken: String)

class AuthService {

    /**
     * Valida credenciales contra la tabla interna 'usuarios' en Supabase.
     * NO usa Supabase Auth (signInWith) ya que este espera email/password.
     */
    suspend fun login(phone: String, password: String): Result<AuthSession> =
        withContext(Dispatchers.IO) {
            runCatching {
                // 1. Consultar usuario por número de teléfono
                val response = supabaseClient.from("usuarios")
                    .select {
                        filter {
                            eq("numero_telefono", phone)
                        }
                    }.decodeSingleOrNull<JsonObject>()
                    ?: throw Exception("invalid_credentials")

                // 2. Validar contraseña (según lógica existente: comparación directa)
                val storedPassword = response["contrasena"]?.jsonPrimitive?.content
                
                if (storedPassword == password) {
                    // 3. Login exitoso - Retornar sesión local para el SessionManager
                    AuthSession(
                        accessToken  = "session_$phone",
                        refreshToken = "refresh_$phone",
                    )
                } else {
                    throw Exception("invalid_credentials")
                }
            }
        }
}
