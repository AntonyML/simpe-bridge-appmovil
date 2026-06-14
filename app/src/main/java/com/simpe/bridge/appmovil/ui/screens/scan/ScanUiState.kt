package com.simpe.bridge.appmovil.ui.screens.scan

import android.net.Uri
import com.simpe.bridge.appmovil.domain.receipt.OptimizedReceiptImage
import com.simpe.bridge.appmovil.domain.receipt.ReceiptCaptureRecord
import com.simpe.bridge.appmovil.domain.receipt.ReceiptQualityReport

sealed interface ScanStage {
    object Idle : ScanStage
    object Capturing : ScanStage
    object Processing : ScanStage
    object Validating : ScanStage
    object Passed : ScanStage
    object Failed : ScanStage
    object Uploading : ScanStage
    object Uploaded : ScanStage
    data class Error(val message: String) : ScanStage
}

data class ScanUiState(
    val stage: ScanStage = ScanStage.Idle,
    val qualityReport: ReceiptQualityReport? = null,
    val optimizedImage: OptimizedReceiptImage? = null,
    val previewUri: Uri? = null,
    val captureId: String? = null,
    val history: List<ReceiptCaptureRecord> = emptyList(),
    val uploadedMessage: String? = null,
    val correlationToken: String = "",
) {
    val canUpload: Boolean = stage is ScanStage.Passed && optimizedImage != null && correlationToken.isNotBlank()
    val canRetry: Boolean = stage is ScanStage.Failed || stage is ScanStage.Passed || stage is ScanStage.Error || stage is ScanStage.Uploaded
    val isBusy: Boolean = stage is ScanStage.Capturing || stage is ScanStage.Processing || stage is ScanStage.Validating || stage is ScanStage.Uploading
}
