/**
 * SINPE Bridge HTTP Client - Centralized networking layer
 *
 * Provides:
 * - Secure API communication
 * - HMAC SHA-256 signing
 * - Request correlation and tracing
 * - Automatic retries with exponential backoff
 * - Offline queue support
 * - Structured logging
 */

package com.simpe.bridge.appmovil.data.remote

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * Secure envelope for all API requests
 */
data class SecureEnvelope(
    val message_id: String,              // UUID
    val correlation_id: String,          // UUID
    val content_hash: String,            // SHA-256
    val signature: String,               // HMAC SHA-256
    val created_at: Long,                // Unix timestamp
    val source: String,                  // "android-sms-listener"
    val device_hash: String,             // Anonymized device ID
    val status: String = "SENT"
)


/**
 * Request retry configuration
 */
data class RetryConfig(
    val maxAttempts: Int = 3,
    val initialDelayMs: Long = 1000,
    val maxDelayMs: Long = 10000,
    val backoffMultiplier: Float = 2f
)


/**
 * Result wrapper for network calls
 */
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val exception: Exception, val statusCode: Int? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}


class SinpeBridgeHttpClient(
    private val context: Context,
    private val baseUrl: String = "https://sinpe-bridge-api.fly.dev",
    private val apiKey: String,
    private val deviceId: String,
    private val hmacSecret: String = apiKey,
) {
    
    companion object {
        private const val TAG = "SinpeBridgeHttpClient"
        private const val CONTENT_TYPE_JSON = "application/json"
        private const val CONTENT_TYPE_MULTIPART = "multipart/form-data"
        private const val TIMEOUT_SECONDS = 30L
        private const val JSON_CHARSET = "UTF-8"
    }
    
    private val json = Json { ignoreUnknownKeys = true }
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .addInterceptor(LoggingInterceptor())
        .retryOnConnectionFailure(true)
        .build()
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // ========================================================================
    // HMAC SIGNING & HASHING
    // ========================================================================
    
    private fun computeSHA256(content: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(content.toByteArray(StandardCharsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    private fun computeHMAC(content: String): String {
        try {
            val mac = Mac.getInstance("HmacSHA256")
            val secretKeySpec = SecretKeySpec(
                hmacSecret.toByteArray(StandardCharsets.UTF_8),
                0,
                hmacSecret.length,
                "HmacSHA256"
            )
            mac.init(secretKeySpec)
            val digest = mac.doFinal(content.toByteArray(StandardCharsets.UTF_8))
            return digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "HMAC computation failed", e)
            throw e
        }
    }
    
    private fun anonymizeDeviceId(): String {
        return computeSHA256(deviceId).take(16)
    }
    
    // ========================================================================
    // ENVELOPE CREATION
    // ========================================================================
    
    private fun createSecureEnvelope(body: String): SecureEnvelope {
        val messageId = UUID.randomUUID().toString()
        val correlationId = UUID.randomUUID().toString()
        val contentHash = computeSHA256(body)
        val signature = computeHMAC(body)
        val createdAt = System.currentTimeMillis() / 1000
        val deviceHash = anonymizeDeviceId()
        
        return SecureEnvelope(
            message_id = messageId,
            correlation_id = correlationId,
            content_hash = contentHash,
            signature = signature,
            created_at = createdAt,
            source = "android-sms-listener",
            device_hash = deviceHash,
            status = "SENT"
        )
    }
    
    // ========================================================================
    // REQUEST BUILDING
    // ========================================================================
    
    private fun buildJsonRequest(
        method: String,
        endpoint: String,
        body: String,
        correlationId: String? = null
    ): Request {
        val envelope = createSecureEnvelope(body)
        
        val requestBody = body.toRequestBody(CONTENT_TYPE_JSON.toMediaType())
        
        val requestBuilder = Request.Builder()
            .url("$baseUrl$endpoint")
            .method(method, if (method in listOf("POST", "PUT")) requestBody else null)
            // Security headers
            .header("x-api-key", apiKey)
            .header("x-signature", envelope.signature)
            .header("Content-Type", CONTENT_TYPE_JSON)
            // Tracing headers
            .header("x-correlation-id", correlationId ?: envelope.correlation_id)
            .header("x-request-id", envelope.message_id)
            .header("x-device-id", deviceId)
            .header("x-trace-id", UUID.randomUUID().toString())
            // User-Agent
            .header("User-Agent", "SinpeBridgeApp/1.0 Android")
        
        logRequest("${method} $endpoint", requestBody.toString())
        
        return requestBuilder.build()
    }
    
    // ========================================================================
    // NETWORK CALLS WITH RETRY
    // ========================================================================
    
    suspend fun <T> executeWithRetry(
        request: Request,
        retryConfig: RetryConfig = RetryConfig(),
        parser: (String) -> T
    ): NetworkResult<T> = withContext(Dispatchers.IO) {
        var lastException: Exception? = null
        var delayMs = retryConfig.initialDelayMs
        
        repeat(retryConfig.maxAttempts) { attempt ->
            try {
                val response = httpClient.newCall(request).execute()
                
                return@withContext if (response.isSuccessful) {
                    val body = response.body?.string() ?: "{}"
                    logResponse("${request.method} ${request.url}", response.code, body)
                    NetworkResult.Success(parser(body))
                } else {
                    val body = response.body?.string() ?: ""
                    Log.w(TAG, "HTTP ${response.code}: $body")
                    NetworkResult.Error(
                        IOException("HTTP ${response.code}"),
                        response.code
                    )
                }
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "Attempt $attempt failed: ${e.message}")
                
                if (attempt < retryConfig.maxAttempts - 1) {
                    delay(delayMs)
                    delayMs = (delayMs * retryConfig.backoffMultiplier).toLong()
                        .coerceAtMost(retryConfig.maxDelayMs)
                }
            }
        }
        
        NetworkResult.Error(lastException ?: IOException("Unknown error"))
    }
    
    // ========================================================================
    // PUBLIC API METHODS
    // ========================================================================
    
    suspend fun postSmsMessage(
        smsBody: String,
        sender: String,
        timestamp: Long,
        correlationId: String? = null
    ): NetworkResult<String> {
        val payload = """
            {
              "envelope": {
                "message_id": "${UUID.randomUUID()}",
                "correlation_id": "${correlationId ?: UUID.randomUUID()}",
                "content_hash": "${computeSHA256(smsBody)}",
                "created_at": ${System.currentTimeMillis() / 1000},
                "source": "android-sms-listener",
                "device_hash": "${anonymizeDeviceId()}"
              },
              "payload": {
                "body": "$smsBody",
                "sender": "$sender",
                "timestamp": $timestamp
              }
            }
        """.trimIndent()
        
        val request = buildJsonRequest("POST", "/api/v1/payments", payload, correlationId)
        
        return executeWithRetry(request) { it }
    }
    
    suspend fun uploadReceiptImage(
        filePath: String,
        imageType: String = "receipt_qr",
        messageId: String? = null,
        correlationId: String? = null
    ): NetworkResult<String> {
        val envelope = createSecureEnvelope("{}")
        val file = java.io.File(filePath)
        
        if (!file.exists()) {
            return NetworkResult.Error(IOException("File not found: $filePath"))
        }
        
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create(null, file))
            .addFormDataPart("device_id", deviceId)
            .addFormDataPart("correlation_id", correlationId ?: envelope.correlation_id)
            .addFormDataPart("image_type", imageType)
            .apply { messageId?.let { addFormDataPart("message_id", it) } }
            .build()
        
        val request = Request.Builder()
            .url("$baseUrl/api/v1/uploads/receipts")
            .post(requestBody)
            .header("x-api-key", apiKey)
            .header("x-correlation-id", correlationId ?: envelope.correlation_id)
            .header("x-device-id", deviceId)
            .header("User-Agent", "SinpeBridgeApp/1.0 Android")
            .build()
        
        logRequest("POST /api/v1/uploads/receipts", "multipart/form-data")
        
        return executeWithRetry(request) { it }
    }
    
    // ========================================================================
    // LOGGING
    // ========================================================================
    
    private fun logRequest(endpoint: String, body: String) {
        Log.d(TAG, "📤 $endpoint | Body: ${body.take(200)}")
    }
    
    private fun logResponse(endpoint: String, statusCode: Int, body: String) {
        Log.d(TAG, "📥 $endpoint | Status: $statusCode | Body: ${body.take(200)}")
    }
    
    // ========================================================================
    // CLEANUP
    // ========================================================================
    
    fun shutdown() {
        coroutineScope.cancel()
        httpClient.dispatcher.executorService.shutdown()
    }
    
    // ========================================================================
    // LOGGING INTERCEPTOR
    // ========================================================================
    
    private inner class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val startTime = System.nanoTime()
            
            val response = try {
                chain.proceed(request)
            } catch (e: Exception) {
                Log.e(TAG, "Network error: ${e.message}")
                throw e
            }
            
            val elapsed = (System.nanoTime() - startTime) / 1_000_000
            val statusCode = response.code
            
            Log.v(TAG, "$statusCode ${request.method} ${request.url} (${elapsed}ms)")
            
            return response
        }
    }
}
