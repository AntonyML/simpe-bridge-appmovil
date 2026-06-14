package com.simpe.bridge.appmovil.data.auth

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabaseClient = createSupabaseClient(
    supabaseUrl = "https://ffqudsnjmkbgvbrlsiov.supabase.co",
    supabaseKey = "sb_publishable_fWksHYyft-tjswSfnQpQpQ_-P9eP78-"
) {
    install(Auth)
    install(Postgrest)
}
