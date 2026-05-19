package com.simpe.bridge.appmovil.domain.usecases.receipt

import android.graphics.BitmapFactory
import com.simpe.bridge.appmovil.domain.receipt.OptimizedReceiptImage
import com.simpe.bridge.appmovil.domain.receipt.ReceiptQualityReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadRepository
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadRequest
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadResult
import com.simpe.bridge.appmovil.utils.ReceiptImageOptimizer
import com.simpe.bridge.appmovil.validation.ReceiptQualityAnalyzer
import java.io.File

class AnalyzeReceiptQualityUseCase(
    private val analyzer: ReceiptQualityAnalyzer,
) {
    operator fun invoke(imageFile: File): ReceiptQualityReport {
        val bitmap = requireNotNull(BitmapFactory.decodeFile(imageFile.absolutePath)) {
            "No se pudo leer la captura"
        }
        return try {
            analyzer.analyze(bitmap)
        } finally {
            bitmap.recycle()
        }
    }
}

class OptimizeReceiptImageUseCase(
    private val optimizer: ReceiptImageOptimizer,
) {
    operator fun invoke(imageFile: File): OptimizedReceiptImage {
        return optimizer.optimize(imageFile)
    }
}

class UploadReceiptUseCase(
    private val repository: ReceiptUploadRepository,
) {
    suspend operator fun invoke(request: ReceiptUploadRequest): ReceiptUploadResult {
        return repository.upload(request)
    }
}
