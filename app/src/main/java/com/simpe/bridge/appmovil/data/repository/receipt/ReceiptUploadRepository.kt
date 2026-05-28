/**
 * Upload Repository for SINPE Bridge Android App
 *
 * Provides:
 * - Receipt image uploads
 * - QR code uploads
 * - Offline queue for failed uploads
 * - Retry logic with Room persistence
 */

package com.simpe.bridge.appmovil.data.repository.receipt

import java.util.*

/**
 * Data class for upload request metadata
 */
data class UploadRequest(
    val id: String = UUID.randomUUID().toString(),
    val filePath: String,
    val imageType: String,
    val messageId: String? = null,
    val correlationId: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val status: String = "PENDING",  // PENDING, UPLOADING, SUCCESS, FAILED
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val errorMessage: String? = null,
)
