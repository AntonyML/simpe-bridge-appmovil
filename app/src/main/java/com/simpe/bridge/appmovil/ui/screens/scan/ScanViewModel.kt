package com.simpe.bridge.appmovil.ui.screens.scan

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simpe.bridge.appmovil.BuildConfig
import com.simpe.bridge.appmovil.data.local.AppDatabase
import com.simpe.bridge.appmovil.data.repository.receipt.ReceiptCaptureLocalRepository
import com.simpe.bridge.appmovil.data.repository.receipt.ReceiptUploadRepositoryImpl
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadRequest
import com.simpe.bridge.appmovil.domain.usecases.receipt.AnalyzeReceiptQualityUseCase
import com.simpe.bridge.appmovil.domain.usecases.receipt.AnalyzeReceiptSemanticUseCase
import com.simpe.bridge.appmovil.domain.usecases.receipt.BuildReceiptFinalReportUseCase
import com.simpe.bridge.appmovil.domain.usecases.receipt.EstimateReceiptLikelihoodUseCase
import com.simpe.bridge.appmovil.domain.usecases.receipt.OptimizeReceiptImageUseCase
import com.simpe.bridge.appmovil.domain.usecases.receipt.UploadReceiptUseCase
import com.simpe.bridge.appmovil.utils.ReceiptImageOptimizer
import com.simpe.bridge.appmovil.validation.ReceiptFinalAggregator
import com.simpe.bridge.appmovil.validation.ReceiptLikelihoodEstimator
import com.simpe.bridge.appmovil.validation.ReceiptOcrEngine
import com.simpe.bridge.appmovil.validation.ReceiptQualityAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ScanViewModel(
    private val appContext: Context,
    private val analyzeReceiptQualityUseCase: AnalyzeReceiptQualityUseCase,
    private val analyzeReceiptSemanticUseCase: AnalyzeReceiptSemanticUseCase,
    private val estimateReceiptLikelihoodUseCase: EstimateReceiptLikelihoodUseCase,
    private val buildReceiptFinalReportUseCase: BuildReceiptFinalReportUseCase,
    private val optimizeReceiptImageUseCase: OptimizeReceiptImageUseCase,
    private val uploadReceiptUseCase: UploadReceiptUseCase,
    private val localRepository: ReceiptCaptureLocalRepository,
    private val ocrEngine: ReceiptOcrEngine,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            localRepository.getHistory().collectLatest { history ->
                _uiState.update { it.copy(history = history) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ocrEngine.close()
    }

    fun onCaptureStarted() {
        _uiState.update {
            it.copy(
                stage = ScanStage.Capturing,
                uploadedMessage = null,
                semanticReport = null,
                likelihoodReport = null,
                finalReport = null,
            )
        }
    }

    fun onCaptureFailed(message: String) {
        _uiState.update { it.copy(stage = ScanStage.Error(message)) }
    }

    fun onTokenChanged(token: String) {
        _uiState.update { it.copy(correlationToken = token) }
    }

    fun processCapture(file: File) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(stage = ScanStage.Processing, previewUri = android.net.Uri.fromFile(file)) }

                val visual = withContext(Dispatchers.Default) {
                    analyzeReceiptQualityUseCase(file)
                }
                _uiState.update { it.copy(qualityReport = visual) }

                val semantic = try {
                    withContext(Dispatchers.IO) { analyzeReceiptSemanticUseCase(file) }
                } catch (error: Throwable) {
                    com.simpe.bridge.appmovil.domain.receipt.ReceiptSemanticReport(
                        score = 0,
                        ocrText = "",
                        normalizedText = "",
                        lineCount = 0,
                        blockCount = 0,
                        characterCount = 0,
                        wordCount = 0,
                        keywordHits = emptyList(),
                        structureHits = emptyList(),
                        screenshotSignals = emptyList(),
                        passed = false,
                    )
                }
                _uiState.update { it.copy(semanticReport = semantic) }

                _uiState.update { it.copy(stage = ScanStage.Validating) }

                val likelihood = withContext(Dispatchers.Default) {
                    estimateReceiptLikelihoodUseCase(visual, semantic)
                }
                _uiState.update { it.copy(likelihoodReport = likelihood) }

                val finalReport = withContext(Dispatchers.Default) {
                    buildReceiptFinalReportUseCase(visual, semantic, likelihood)
                }
                _uiState.update { it.copy(finalReport = finalReport) }

                if (!finalReport.passed) {
                    val captureId = withContext(Dispatchers.IO) {
                        localRepository.saveCapture(
                            sourceFile = file,
                            report = visual,
                            optimizedImage = null,
                            semanticReport = semantic,
                            finalReport = finalReport,
                        )
                    }
                    _uiState.update {
                        it.copy(
                            stage = ScanStage.Failed,
                            qualityReport = visual,
                            semanticReport = semantic,
                            likelihoodReport = likelihood,
                            finalReport = finalReport,
                            optimizedImage = null,
                            captureId = captureId,
                        )
                    }
                    return@launch
                }

                val optimized = withContext(Dispatchers.IO) {
                    optimizeReceiptImageUseCase(file)
                }
                val captureId = withContext(Dispatchers.IO) {
                    localRepository.saveCapture(
                        sourceFile = file,
                        report = visual,
                        optimizedImage = optimized,
                        semanticReport = semantic,
                        finalReport = finalReport,
                    )
                }
                _uiState.update {
                    it.copy(
                        stage = ScanStage.Passed,
                        qualityReport = visual,
                        semanticReport = semantic,
                        likelihoodReport = likelihood,
                        finalReport = finalReport,
                        optimizedImage = optimized,
                        previewUri = optimized.thumbnailUri,
                        captureId = captureId,
                    )
                }
            } catch (error: Throwable) {
                _uiState.update { it.copy(stage = ScanStage.Error(error.message ?: "Error procesando comprobante")) }
            }
        }
    }

    fun upload() {
        val optimized = uiState.value.optimizedImage ?: return
        val captureId = uiState.value.captureId
        val token = uiState.value.correlationToken

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(stage = ScanStage.Uploading) }
                val result = uploadReceiptUseCase(
                    ReceiptUploadRequest(
                        optimizedImage = optimized,
                        deviceId = deviceId(),
                        timestamp = System.currentTimeMillis(),
                        appVersion = BuildConfig.VERSION_NAME,
                        correlationToken = token
                    )
                )
                if (captureId != null) {
                    localRepository.markUploaded(captureId, result.message)
                }
                _uiState.update {
                    it.copy(
                        stage = ScanStage.Uploaded,
                        uploadedMessage = result.message,
                    )
                }
            } catch (error: Throwable) {
                _uiState.update { it.copy(stage = ScanStage.Error(error.message ?: "Error enviando comprobante")) }
            }
        }
    }

    fun retry() {
        _uiState.value.optimizedImage?.let { optimized ->
            listOfNotNull(optimized.imageUri.path, optimized.thumbnailUri.path).forEach { path ->
                runCatching { File(path).delete() }
            }
        }
        _uiState.value.previewUri?.path?.let { path ->
            runCatching { File(path).delete() }
        }
        _uiState.update { current ->
            ScanUiState(
                history = current.history,
                correlationToken = current.correlationToken,
            )
        }
    }

    private fun deviceId(): String {
        return Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "android-${BuildConfig.APPLICATION_ID}"
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val analyzer = ReceiptQualityAnalyzer()
                    val ocrEngine = ReceiptOcrEngine()
                    val likelihoodEstimator = ReceiptLikelihoodEstimator()
                    val finalAggregator = ReceiptFinalAggregator()
                    val optimizer = ReceiptImageOptimizer(appContext)
                    val repository = ReceiptUploadRepositoryImpl(appContext)
                    val localRepository = ReceiptCaptureLocalRepository(
                        context = appContext,
                        dao = AppDatabase.getInstance(appContext).receiptCaptureDao()
                    )
                    @Suppress("UNCHECKED_CAST")
                    return ScanViewModel(
                        appContext = appContext,
                        analyzeReceiptQualityUseCase = AnalyzeReceiptQualityUseCase(analyzer),
                        analyzeReceiptSemanticUseCase = AnalyzeReceiptSemanticUseCase(ocrEngine),
                        estimateReceiptLikelihoodUseCase = EstimateReceiptLikelihoodUseCase(likelihoodEstimator),
                        buildReceiptFinalReportUseCase = BuildReceiptFinalReportUseCase(finalAggregator),
                        optimizeReceiptImageUseCase = OptimizeReceiptImageUseCase(optimizer),
                        uploadReceiptUseCase = UploadReceiptUseCase(repository),
                        localRepository = localRepository,
                        ocrEngine = ocrEngine,
                    ) as T
                }
            }
        }
    }
}
