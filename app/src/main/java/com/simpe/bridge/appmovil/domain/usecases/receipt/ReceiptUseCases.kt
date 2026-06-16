package com.simpe.bridge.appmovil.domain.usecases.receipt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.simpe.bridge.appmovil.domain.receipt.OptimizedReceiptImage
import com.simpe.bridge.appmovil.domain.receipt.ReceiptFinalReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptLikelihoodReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptQualityReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptSemanticReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadRepository
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadRequest
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadResult
import com.simpe.bridge.appmovil.utils.ReceiptImageOptimizer
import com.simpe.bridge.appmovil.validation.ReceiptFinalAggregator
import com.simpe.bridge.appmovil.validation.ReceiptLikelihoodEstimator
import com.simpe.bridge.appmovil.validation.ReceiptOcrEngine
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

class AnalyzeReceiptSemanticUseCase(
    private val ocrEngine: ReceiptOcrEngine,
) {
    suspend operator fun invoke(imageFile: File): ReceiptSemanticReport {
        val options = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
        val bitmap = requireNotNull(BitmapFactory.decodeFile(imageFile.absolutePath, options)) {
            "No se pudo leer la captura para OCR"
        }
        return try {
            val extraction = ocrEngine.recognize(bitmap)
            ocrEngine.buildReport(extraction)
        } finally {
            bitmap.recycle()
        }
    }
}

class EstimateReceiptLikelihoodUseCase(
    private val estimator: ReceiptLikelihoodEstimator,
) {
    operator fun invoke(
        visual: ReceiptQualityReport,
        semantic: ReceiptSemanticReport,
    ): ReceiptLikelihoodReport = estimator.estimate(visual, semantic)
}

class BuildReceiptFinalReportUseCase(
    private val aggregator: ReceiptFinalAggregator,
) {
    operator fun invoke(
        visual: ReceiptQualityReport,
        semantic: ReceiptSemanticReport,
        likelihood: ReceiptLikelihoodReport,
    ): ReceiptFinalReport = aggregator.aggregate(visual, semantic, likelihood)
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
