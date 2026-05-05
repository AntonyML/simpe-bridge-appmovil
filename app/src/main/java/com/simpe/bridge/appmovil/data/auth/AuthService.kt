// ─────────────────────────────────────────────────────────────────────────────
// TOMBSTONE — this file has been replaced as part of the auth refactor.
//
// AuthService was dissolved into two focused classes:
//
//   MOVED TO → data/remote/auth/SupabaseAuthDataSource.kt
//               (raw Supabase SDK calls: signInWithEmail, signOut)
//
//   MOVED TO → data/repository/AuthRepositoryImpl.kt
//               (phone→email mapping, session persistence, profile upsert)
//
// DO NOT add new code here.
// Delete this file once you confirm no other class imports it.
// ─────────────────────────────────────────────────────────────────────────────
package com.simpe.bridge.appmovil.data.auth
