/**
 * OkHttp Interceptor for SINPE Bridge
 *
 * Automatically adds:
 * - Correlation IDs
 * - Request IDs
 * - Trace IDs
 * - Device identifiers
 * - Request timing
 */

package com.simpe.bridge.appmovil.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*


/**
 * Adds tracing and correlation headers to all requests
 */
class TracingInterceptor(
    private val deviceId: String,
) : Interceptor {
    
    companion object {
        private const val TAG = "TracingInterceptor"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Generate trace IDs
        val correlationId = UUID.randomUUID().toString()
        val requestId = UUID.randomUUID().toString()
        val traceId = UUID.randomUUID().toString()
        
        // Add trace headers
        val enhancedRequest = originalRequest.newBuilder()
            .header("x-correlation-id", correlationId)
            .header("x-request-id", requestId)
            .header("x-trace-id", traceId)
            .header("x-device-id", deviceId)
            .header("x-forwarded-time", System.currentTimeMillis().toString())
            .build()
        
        val startTime = System.nanoTime()
        
        return try {
            val response = chain.proceed(enhancedRequest)
            val elapsed = (System.nanoTime() - startTime) / 1_000_000
            
            Log.v(TAG, """
                📋 Request: ${enhancedRequest.method} ${enhancedRequest.url}
                ├─ correlation_id: $correlationId
                ├─ request_id: $requestId
                ├─ trace_id: $traceId
                ├─ status: ${response.code}
                └─ elapsed: ${elapsed}ms
            """.trimIndent())
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Request failed: ${e.message}", e)
            throw e
        }
    }
}


/**
 * Adds API key to requests (for header-based auth)
 */
class ApiKeyInterceptor(
    private val apiKey: String,
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Add API key only if not already present
        val request = if (!originalRequest.headers.names().contains("x-api-key")) {
            originalRequest.newBuilder()
                .header("x-api-key", apiKey)
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(request)
    }
}


/**
 * Logs request/response bodies for debugging
 */
class RequestResponseLoggingInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "RequestResponseLogging"
        private const val MAX_BODY_LENGTH = 500
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Log request
        val requestBody = request.body
        var requestBodyStr = ""
        if (requestBody != null) {
            val buffer = okio.Buffer()
            requestBody.writeTo(buffer)
            requestBodyStr = buffer.readUtf8().take(MAX_BODY_LENGTH)
        }
        
        Log.d(TAG, "→ REQUEST: ${request.method} ${request.url}")
        if (requestBodyStr.isNotEmpty()) {
            Log.d(TAG, "  Body: $requestBodyStr")
        }
        
        // Process response
        val startTime = System.nanoTime()
        val response = chain.proceed(request)
        val elapsed = (System.nanoTime() - startTime) / 1_000_000
        
        // Log response
        Log.d(TAG, "← RESPONSE: ${response.code} (${elapsed}ms)")
        
        return response
    }
}
