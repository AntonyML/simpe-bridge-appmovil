package com.simpe.bridge.appmovil.data.remote.auth

import com.simpe.bridge.appmovil.data.auth.supabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * SupabaseProfileDataSource — manages the "profiles" table in Supabase.
 *
 * The "profiles" table is the bridge between Supabase Auth (auth.users)
 * and the application's business data. Each row is keyed by the same UUID
 * used in auth.users, enforced by a foreign key constraint in the database.
 *
 * Responsibilities:
 *   - Fetch a profile by user ID
 *   - Create a profile on first login
 *
 * NOT responsible for:
 *   - Authentication (that is SupabaseAuthDataSource's job)
 *   - Domain mapping (the Repository converts ProfileRecord → UserProfile)
 */
class SupabaseProfileDataSource {

    /**
     * Returns the profile matching [userId], or null if it does not exist yet.
     * A null result means the user authenticated but has never completed profile setup.
     */
    suspend fun getProfile(userId: String): Result<ProfileRecord?> =
        withContext(Dispatchers.IO) {
            runCatching {
                supabaseClient
                    .from("profiles")
                    .select {
                        filter {
                            FilterOperation("id", FilterOperator.EQ, userId)
                        }
                    }
                    .decodeSingleOrNull<ProfileRecord>()
            }
        }

    /**
     * Inserts a new profile row for [userId].
     * Must only be called after confirming the profile does not already exist.
     *
     * @param userId  UUID from auth.users — must match exactly
     * @param phone   The user's actual phone number (e.g. "88887777")
     */
    suspend fun createProfile(userId: String, phone: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                supabaseClient
                    .from("profiles")
                    .insert(ProfileRecord(id = userId, phone = phone))
                Unit
            }
        }
}

/**
 * Wire-format representation of a row in the "profiles" Supabase table.
 *
 * @Serializable is required for the Supabase Kotlin SDK's Postgrest module.
 * This class must NOT be used outside the data layer.
 * The domain receives [com.simpe.bridge.appmovil.domain.auth.model.UserProfile] instead.
 */
@Serializable
data class ProfileRecord(
    @SerialName("id")    val id: String,
    @SerialName("phone") val phone: String
)
