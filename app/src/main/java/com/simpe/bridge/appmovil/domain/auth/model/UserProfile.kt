package com.simpe.bridge.appmovil.domain.auth.model

/**
 * UserProfile — pure domain model representing a user's business identity.
 *
 * This class has:
 *   - NO Supabase imports
 *   - NO Android imports
 *   - NO serialization annotations
 *   - NO database column names
 *
 * It represents what the application CARES about, not how data is stored.
 *
 * Separation of concerns:
 *   auth.users (Supabase)  → handles WHO the user is (authentication)
 *   UserProfile (domain)   → handles WHAT we know about the user (business data)
 *
 * The [id] field is the same UUID as auth.users.id, making it trivial
 * to enforce Row Level Security (RLS) policies in Supabase.
 */
data class UserProfile(
    val id: String,
    val phone: String
)
