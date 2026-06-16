package com.simpe.bridge.appmovil.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "receipt_captures",
    indices = [Index(value = ["sha256"], unique = false)]
)
data class ReceiptCaptureEntity(
    @PrimaryKey
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
    val metadataJson: String,
    val checklistJson: String,
    val error: String?,
    val visualScore: Int = score,
    val semanticScore: Int = 0,
    val likelihoodScore: Int = 0,
    val finalScore: Int = score,
    val rejectionReasonsJson: String = "[]",
    val ocrSummary: String = "",
)
