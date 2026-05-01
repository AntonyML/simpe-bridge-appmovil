package com.simpe.bridge.appmovil.data.auth

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabaseClient = createSupabaseClient(
    supabaseUrl = "https://ichekhsyjjcdlnknaftf.supabase.co",
    supabaseKey = "sb_publishable_UDn8FdkBmpmnYbaSgWP6Lg_y3mVfnEJ"
) {
    install(Auth)
    install(Postgrest)
}
