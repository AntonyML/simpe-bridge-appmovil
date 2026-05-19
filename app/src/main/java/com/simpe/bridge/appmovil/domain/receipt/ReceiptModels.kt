package com.simpe.bridge.appmovil.domain.receipt

import android.net.Uri

data class ReceiptQualityReport(
    val score: Int,
    val blur: QualityMetric,
    val brightness: QualityMetric,
    val contrast: QualityMetric,
    val textVisibility: QualityMetric,
    val framing: QualityMetric,
    val perspective: QualityMetric,
    val resolution: QualityMetric,
) {
    val passed: Boolean = score >= PASS_THRESHOLD

    companion object {
        const val PASS_THRESHOLD = 80
    }
}

data class QualityMetric(
    val key: String,
    val label: String,
    val score: Int,
    val passed: Boolean,
    val detail: String,
)

data class OptimizedReceiptImage(
    val imageUri: Uri,
    val thumbnailUri: Uri,
    val sha256: String,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val metadata: ReceiptImageMetadata,
)

data class ReceiptImageMetadata(
    val originalWidth: Int,
    val originalHeight: Int,
    val processedWidth: Int,
    val processedHeight: Int,
    val quality: Int,
    val mimeType: String = "image/webp",
)

data class ReceiptUploadRequest(
    val optimizedImage: OptimizedReceiptImage,
    val deviceId: String,
    val timestamp: Long,
    val appVersion: String,
)

data class ReceiptUploadResult(
    val remoteId: String,
    val duplicate: Boolean,
    val message: String,
)

interface ReceiptUploadRepository {
    suspend fun upload(request: ReceiptUploadRequest): ReceiptUploadResult
}
