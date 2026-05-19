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
)
