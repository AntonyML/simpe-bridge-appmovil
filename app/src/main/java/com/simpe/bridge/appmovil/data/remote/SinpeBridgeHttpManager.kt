/**
 * SINPE Bridge HTTP Manager
 *
 * Singleton for managing centralized HTTP client across the app.
 * Handles initialization, configuration, and lifecycle.
 */

package com.simpe.bridge.appmovil.data.remote

import android.content.Context
import android.provider.Settings
import android.util.Log
import java.util.UUID


/**
 * Configuration for SINPE Bridge HTTP client
 */
data class HttpClientConfig(
    val baseUrl: String = "https://api.tonyml.com",
    val apiKey: String,
    val hmacSecret: String? = null,  // If null, uses apiKey
    val timeoutSeconds: Long = 30,
    val enableLogging: Boolean = false,
)


/**
 * Singleton manager for HTTP client
 */
object SinpeBridgeHttpManager {
    
    companion object {
        private const val TAG = "SinpeBridgeHttpManager"
    }
    
    private var httpClient: SinpeBridgeHttpClient? = null
    private var isInitialized = false
    
    @Synchronized
    fun initialize(context: Context, config: HttpClientConfig) {
        if (isInitialized) {
            Log.w(TAG, "Already initialized, skipping")
            return
        }
        
        val deviceId = getOrCreateDeviceId(context)
        
        httpClient = SinpeBridgeHttpClient(
            context = context,
            baseUrl = config.baseUrl,
            apiKey = config.apiKey,
            deviceId = deviceId,
            hmacSecret = config.hmacSecret ?: config.apiKey,
        )
        
        isInitialized = true
        Log.d(TAG, "HTTP Client initialized: deviceId=$deviceId, baseUrl=${config.baseUrl}")
    }
    
    @Synchronized
    fun getClient(): SinpeBridgeHttpClient {
        if (!isInitialized) {
            throw IllegalStateException("HTTP Client not initialized. Call initialize() first.")
        }
        return httpClient!!
    }
    
    @Synchronized
    fun isInitialized(): Boolean = isInitialized
    
    @Synchronized
    fun shutdown() {
        httpClient?.shutdown()
        httpClient = null
        isInitialized = false
        Log.d(TAG, "HTTP Client shutdown")
    }
    
    /**
     * Get or create a persistent device identifier
     */
    private fun getOrCreateDeviceId(context: Context): String {
        val sharedPrefs = context.getSharedPreferences(
            "sinpe_bridge_prefs",
            Context.MODE_PRIVATE
        )
        
        val key = "device_id"
        var deviceId = sharedPrefs.getString(key, null)
        
        if (deviceId == null) {
            // Generate new device ID
            deviceId = UUID.randomUUID().toString()
            sharedPrefs.edit().putString(key, deviceId).apply()
            Log.d(TAG, "Generated new device ID: $deviceId")
        } else {
            Log.d(TAG, "Using existing device ID: $deviceId")
        }
        
        return deviceId
    }
}


/**
 * Extension function for easy access throughout the app
 */
fun Context.getSinpeBridgeHttpClient(): SinpeBridgeHttpClient {
    return SinpeBridgeHttpManager.getClient()
}
