package com.simpe.bridge.appmovil.ui.screens.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.simpe.bridge.appmovil.domain.receipt.QualityMetric
import com.simpe.bridge.appmovil.domain.receipt.ReceiptCaptureRecord
import com.simpe.bridge.appmovil.domain.receipt.ReceiptQualityReport
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = viewModel(factory = ScanViewModel.factory(LocalContext.current)),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var selectedRecord by remember { mutableStateOf<ReceiptCaptureRecord?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val captureEnabled by remember {
        derivedStateOf { hasCameraPermission && !uiState.isBusy && imageCapture != null }
    }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 148.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetShadowElevation = 8.dp,
        sheetContent = {
            if (hasCameraPermission) {
                ScanBottomSheet(
                    uiState = uiState,
                    onRetry = viewModel::retry,
                    onUpload = viewModel::upload,
                    onOpenRecord = { selectedRecord = it },
                )
            } else {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    CameraPermissionPanel(onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) })
                }
            }
        },
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (hasCameraPermission) {
                FixedCameraPreview(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    onImageCaptureReady = { imageCapture = it },
                )
                ReceiptOverlay(isStable = !uiState.isBusy)
                CaptureStatus(stage = uiState.stage)
                CaptureButton(
                    enabled = captureEnabled,
                    onClick = {
                        val capture = imageCapture ?: return@CaptureButton
                        viewModel.onCaptureStarted()
                        val file = context.newReceiptCaptureFile()
                        val output = ImageCapture.OutputFileOptions.Builder(file).build()
                        capture.takePicture(
                            output,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    viewModel.processCapture(file)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    viewModel.onCaptureFailed(exception.message ?: "No se pudo capturar la imagen")
                                }
                            },
                        )
                    },
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    selectedRecord?.let { record ->
        ReceiptImageViewer(
            record = record,
            onDismiss = { selectedRecord = null },
        )
    }
}


@Composable
private fun ScanBottomSheet(
    uiState: ScanUiState,
    onRetry: () -> Unit,
    onUpload: () -> Unit,
    onOpenRecord: (ReceiptCaptureRecord) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(560.dp)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AnalysisPanel(uiState = uiState)
        }
        item {
            ScanActions(
                uiState = uiState,
                onRetry = onRetry,
                onUpload = onUpload,
            )
        }
        item {
            Text(
                text = "Historial local",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        if (uiState.history.isEmpty()) {
            item {
                Text(
                    text = "Sin comprobantes capturados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(
                items = uiState.history,
                key = { it.captureId },
                contentType = { "receipt_history_item" },
            ) { record ->
                ReceiptHistoryItem(
                    record = record,
                    onClick = { onOpenRecord(record) },
                )
            }
        }
    }
}

@Composable
private fun BoxScope.CaptureButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 176.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(20.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.PhotoCamera,
            contentDescription = "Capturar",
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
private fun FixedCameraPreview(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit,
) {
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
    }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(lifecycleOwner, previewView) {
        val executor = ContextCompat.getMainExecutor(context)
        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setJpegQuality(95)
                .build()
            runCatching {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                )
                onImageCaptureReady(imageCapture)
            }
        }
        cameraProviderFuture.addListener(listener, executor)
        onDispose {
            runCatching { cameraProviderFuture.get().unbindAll() }
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView },
    )
}

@Composable
private fun ReceiptOverlay(isStable: Boolean) {
    val guideColor = if (isStable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.12f)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameWidth = size.width * 0.78f
            val frameHeight = size.height * 0.70f
            val left = (size.width - frameWidth) / 2f
            val top = (size.height - frameHeight) / 2f
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.24f),
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx()),
                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f))),
            )
            drawRoundRect(
                color = guideColor,
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx()),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = if (isStable) "Alinee el comprobante completo" else "Mantenga el dispositivo estable",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun BoxScope.CaptureStatus(stage: ScanStage) {
    val text = when (stage) {
        ScanStage.Capturing -> "Capturando"
        ScanStage.Processing -> "Optimizando"
        ScanStage.Validating -> "Validando calidad"
        ScanStage.Uploading -> "Subiendo"
        else -> null
    } ?: return

    Surface(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            Text(text = text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun AnalysisPanel(uiState: ScanUiState) {
    val report = uiState.qualityReport
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Calidad visual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = stageLabel(uiState.stage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                ScoreIndicator(score = report?.score ?: 0)
            }

            report?.let {
                Checklist(report = it)
            } ?: Text(
                text = "Capture el comprobante dentro del marco para analizar enfoque, iluminacion, contraste, texto y encuadre.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            uiState.previewUri?.path?.let { path ->
                ReceiptAsyncImage(
                    path = path,
                    contentDescription = "Comprobante capturado",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    sizePx = 900,
                    contentScale = ContentScale.Crop,
                )
            }

            uiState.optimizedImage?.let { image ->
                Text(
                    text = "Optimizada · ${image.sizeBytes.toHumanFileSize()} · ${image.width}x${image.height}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            uiState.uploadedMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (uiState.stage is ScanStage.Error) {
                Text(
                    text = uiState.stage.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun Checklist(report: ReceiptQualityReport) {
    val items = listOf(
        report.textVisibility,
        report.blur,
        report.brightness,
        report.contrast,
        report.framing,
        report.perspective,
        report.resolution,
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.forEach { metric ->
            MetricRow(metric = metric)
        }
    }
}

@Composable
private fun MetricRow(metric: QualityMetric) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector = if (metric.passed) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                contentDescription = null,
                tint = if (metric.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp),
            )
            Column {
                Text(metric.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(metric.detail, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            text = "${metric.score}%",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (metric.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
private fun ScoreIndicator(score: Int) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(74.dp)) {
        CircularProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 7.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Text(
            text = "$score%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ScanActions(
    uiState: ScanUiState,
    onRetry: () -> Unit,
    onUpload: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilledTonalButton(
            onClick = onRetry,
            enabled = uiState.canRetry && !uiState.isBusy,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text("Nueva foto")
        }
        Button(
            onClick = onUpload,
            enabled = uiState.canUpload && !uiState.isBusy,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Rounded.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text("Subir")
        }
    }
}

@Composable
private fun ReceiptHistoryItem(
    record: ReceiptCaptureRecord,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val previewPath = record.thumbnailPath ?: record.imagePath
            if (previewPath != null) {
                ReceiptAsyncImage(
                    path = previewPath,
                    contentDescription = "Abrir comprobante",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp)),
                    sizePx = 160,
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (record.passed) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = if (record.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Calidad ${record.score}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = record.createdAt.formatRelativeTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = if (record.uploaded) "Subido" else "Local",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (record.uploaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = record.sizeBytes.toHumanFileSize(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReceiptImageViewer(
    record: ReceiptCaptureRecord,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .systemBarsPadding(),
        ) {
            ZoomableReceiptImage(
                path = record.imagePath ?: record.thumbnailPath,
                onDismiss = onDismiss,
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.42f), CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White,
                )
            }
            ReceiptViewerMetadata(
                record = record,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun ZoomableReceiptImage(
    path: String?,
    onDismiss: () -> Unit,
) {
    var scale by remember(path) { mutableStateOf(1f) }
    var offset by remember(path) { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset = if (scale == 1f) Offset.Zero else offset + panChange
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(path, scale) {
                detectTapGestures(
                    onTap = {
                        if (scale == 1f) onDismiss() else {
                            scale = 1f
                            offset = Offset.Zero
                        }
                    },
                    onDoubleTap = { tapOffset ->
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.4f
                            offset = Offset(
                                x = (size.width / 2f - tapOffset.x) * 0.8f,
                                y = (size.height / 2f - tapOffset.y) * 0.8f,
                            )
                        }
                    },
                )
            }
            .transformable(transformState),
        contentAlignment = Alignment.Center,
    ) {
        if (path != null) {
            ReceiptAsyncImage(
                path = path,
                contentDescription = "Comprobante",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 320.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    },
                sizePx = 1800,
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun ReceiptViewerMetadata(
    record: ReceiptCaptureRecord,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Comprobante SINPE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = record.createdAt.formatCaptureDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                ScorePill(score = record.score, passed = record.passed)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MetadataChip(label = if (record.uploaded) "Subido" else "Local")
                MetadataChip(label = record.sizeBytes.toHumanFileSize())
                MetadataChip(label = record.resolutionLabel())
            }
            record.sha256?.let { hash ->
                Text(
                    text = "SHA-256 ${hash.take(12)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ScorePill(
    score: Int,
    passed: Boolean,
) {
    Surface(
        color = if (passed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = "$score%",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (passed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
        )
    }
}

@Composable
private fun MetadataChip(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReceiptAsyncImage(
    path: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    sizePx: Int,
    contentScale: ContentScale,
) {
    val context = LocalContext.current
    val model = remember(path, sizePx) {
        ImageRequest.Builder(context)
            .data(Uri.fromFile(File(path)))
            .size(sizePx)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .crossfade(120)
            .build()
    }
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}

@Composable
private fun CameraPermissionPanel(onRequest: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(Icons.Rounded.PhotoCamera, contentDescription = null, modifier = Modifier.size(48.dp))
            Text("Permiso de camara requerido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "La captura del comprobante se realiza con CameraX dentro de la app.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onRequest) {
                Text("Permitir camara")
            }
        }
    }
}

private fun stageLabel(stage: ScanStage): String {
    return when (stage) {
        ScanStage.Idle -> "Listo para capturar"
        ScanStage.Capturing -> "Capturando imagen"
        ScanStage.Processing -> "Corrigiendo orientacion y analizando imagen"
        ScanStage.Validating -> "Calculando score"
        ScanStage.Passed -> "Aprobado para subida"
        ScanStage.Failed -> "Requiere nueva foto"
        ScanStage.Uploading -> "Enviando multipart/form-data"
        ScanStage.Uploaded -> "Subida completada"
        is ScanStage.Error -> "Error"
    }
}

private fun Context.newReceiptCaptureFile(): File {
    val dir = File(cacheDir, "receipt_capture").apply { mkdirs() }
    return File(dir, "capture_${System.currentTimeMillis()}.jpg")
}

private fun Long.formatCaptureDate(): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(this))
}

private fun Long.formatRelativeTime(): String {
    val diff = max(0L, System.currentTimeMillis() - this)
    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour
    return when {
        diff < minute -> "Ahora"
        diff < hour -> "Hace ${diff / minute} min"
        diff < day -> "Hace ${diff / hour} h"
        diff < 7 * day -> "Hace ${diff / day} d"
        else -> formatCaptureDate()
    }
}

private fun Long.toHumanFileSize(): String {
    if (this <= 0L) return "-"
    val kb = this / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> "${(gb * 10).roundToInt() / 10.0} GB"
        else -> "${(max(0.1, mb) * 10).roundToInt() / 10.0} MB"
    }
}

private fun ReceiptCaptureRecord.resolutionLabel(): String {
    return if (width > 0 && height > 0) "${width}x$height" else "Sin resolucion"
}
