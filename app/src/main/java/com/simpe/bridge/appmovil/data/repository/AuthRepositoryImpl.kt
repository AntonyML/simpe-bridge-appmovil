package com.simpe.bridge.appmovil.data.repository

import com.simpe.bridge.appmovil.data.auth.SessionManager
import com.simpe.bridge.appmovil.data.remote.auth.SupabaseAuthDataSource
import com.simpe.bridge.appmovil.data.remote.auth.SupabaseProfileDataSource
import com.simpe.bridge.appmovil.domain.auth.AuthRepository

/**
 * AuthRepositoryImpl — the single coordinator for the login flow.
 *
 * ┌──────────────────────────────────────────────────────────────────────┐
 * │  DEPENDENCY RULE (strictly enforced):                                │
 * │  ✅ data/repository  may depend on  data/remote  and  data/auth     │
 * │  ✅ data/repository  implements     domain/auth/AuthRepository       │
 * │  ❌ domain           must NOT know  this class exists                │
 * │  ❌ ui               must NOT know  this class exists                │
 * └──────────────────────────────────────────────────────────────────────┘
 *
 * Responsibilities in login order:
 *   1. Translate phone → fake email  (isolated here, invisible to domain/ui)
 *   2. Delegate authentication to    [SupabaseAuthDataSource]
 *   3. Persist session tokens via    [SessionManager]
 *   4. Ensure profile row exists via [SupabaseProfileDataSource]
 *      (creates the profile on first login; no-ops on subsequent logins)
 */
class AuthRepositoryImpl(
    private val authDataSource: SupabaseAuthDataSource,
    private val profileDataSource: SupabaseProfileDataSource,
    private val sessionManager: SessionManager
) : AuthRepository {

    // ── AuthRepository contract ───────────────────────────────────────────

    override suspend fun login(phone: String, password: String): Result<Unit> =
        authDataSource
            .signInWithEmail(phoneToEmail(phone), password)
            .mapCatching { tokens ->
                // Step 1: persist tokens before any other network call
                sessionManager.saveSession(tokens.accessToken, tokens.refreshToken)
                // Step 2: lazily create the profile row on first login
                ensureProfileExists(userId = tokens.userId, phone = phone)
            }

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    override fun logout() {
        sessionManager.clearSession()
        // Note: for a full sign-out also call authDataSource.signOut() asynchronously
        // if you want to invalidate the Supabase session server-side.
    }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Converts a phone number to the synthetic email format required by Supabase Auth.
     *
     * This mapping exists because Supabase Auth's built-in "Phone" provider requires
     * an SMS gateway. Since SIMPE Bridge validates users differently, we use the
     * Email provider with a deterministic fake email domain.
     *
     * This is the ONLY place in the entire codebase where this transformation exists.
     * The domain layer, use cases, and UI never see this detail.
     *
     * Example: "88887777" → "88887777@simpe.bridge"
     */
    private fun phoneToEmail(phone: String): String = "$phone@simpe.bridge"

    /**
     * Checks whether a profile row already exists for [userId].
     * If not, creates one. This is safe to call on every login:
     *   - First login  → profile is created
     *   - Nth login    → profile already exists, nothing happens
     *
     * Profile creation failure is intentionally non-fatal:
     *   The user authenticated successfully and their session is valid.
     *   A profile creation retry can happen the next time they log in,
     *   or you can add a background retry strategy later.
     */
    private suspend fun ensureProfileExists(userId: String, phone: String) {
        val existingProfile = profileDataSource.getProfile(userId).getOrNull()
        if (existingProfile == null) {
            profileDataSource
                .createProfile(userId = userId, phone = phone)
                .onFailure { error ->
                    // TODO: replace with your logging framework (Timber, etc.)
                    System.err.println("[AuthRepository] Profile creation failed for $userId: ${error.message}")
                }
        }
    }
}
