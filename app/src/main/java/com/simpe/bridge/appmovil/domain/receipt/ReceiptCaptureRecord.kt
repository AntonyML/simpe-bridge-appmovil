package com.simpe.bridge.appmovil.domain.receipt

data class ReceiptCaptureRecord(
    val captureId: String,
    val createdAt: Long,
    val imagePath: String?,
    val thumbnailPath: String?,
    val sha256: String?,
    val score: Int,
    val passed: Boolean,
    val uploaded: Boolean,
    val uploadMessage: String?,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val error: String?,
    val visualScore: Int = score,
    val semanticScore: Int = 0,
    val likelihoodScore: Int = 0,
    val finalScore: Int = score,
    val rejectionReasons: List<String> = emptyList(),
    val ocrSummary: String = "",
)
