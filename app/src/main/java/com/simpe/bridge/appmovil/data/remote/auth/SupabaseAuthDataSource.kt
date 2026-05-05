package com.simpe.bridge.appmovil.data.remote.auth

import com.simpe.bridge.appmovil.data.auth.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * SupabaseAuthDataSource — the ONLY class allowed to touch the Supabase Auth SDK.
 *
 * Responsibilities:
 *   - Sign in / sign out via Supabase Auth
 *   - Return raw SDK data as [AuthTokens] (a plain data class with no Supabase types)
 *
 * NOT responsible for:
 *   - Phone → email mapping  (that is the Repository's job)
 *   - Persisting tokens      (that is SessionManager's job)
 *   - Profile management     (that is SupabaseProfileDataSource's job)
 */
class SupabaseAuthDataSource {

    /**
     * Authenticates with Supabase using email + password.
     *
     * The caller is responsible for constructing the email from the phone number.
     * This class never knows about the phone-to-email trick.
     *
     * @param email    The Supabase-registered email (e.g. "88887777@simpe.bridge")
     * @param password The user's password
     * @return [Result] wrapping [AuthTokens] on success, or a Throwable on failure
     */
    suspend fun signInWithEmail(email: String, password: String): Result<AuthTokens> =
        withContext(Dispatchers.IO) {
            runCatching {
                supabaseClient.auth.signInWith(Email) {
                    this.email    = email
                    this.password = password
                }

                val user    = supabaseClient.auth.currentUserOrNull()
                    ?: error("Supabase devolvió sesión pero sin usuario")
                val session = supabaseClient.auth.currentSessionOrNull()
                    ?: error("No se pudo obtener la sesión de Supabase")

                AuthTokens(
                    userId       = user.id,
                    accessToken  = session.accessToken,
                    refreshToken = session.refreshToken ?: ""
                )
            }
        }

    /**
     * Signs the current user out from Supabase.
     * Safe to call even if no session is active.
     */
    suspend fun signOut(): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { supabaseClient.auth.signOut() }
        }
}

/**
 * Plain data class carrying the result of a successful authentication.
 * Contains no Supabase SDK types — safe to use anywhere in the data layer.
 *
 * @param userId       UUID from auth.users (used to link with the profiles table)
 * @param accessToken  JWT for authorising subsequent API calls
 * @param refreshToken Token for renewing the access token silently
 */
data class AuthTokens(
    val userId: String,
    val accessToken: String,
    val refreshToken: String
)
