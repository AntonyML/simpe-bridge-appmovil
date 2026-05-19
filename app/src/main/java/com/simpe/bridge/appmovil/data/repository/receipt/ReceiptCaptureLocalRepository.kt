package com.simpe.bridge.appmovil.data.repository.receipt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.google.gson.Gson
import com.simpe.bridge.appmovil.data.local.ReceiptCaptureDao
import com.simpe.bridge.appmovil.data.local.ReceiptCaptureEntity
import com.simpe.bridge.appmovil.domain.receipt.OptimizedReceiptImage
import com.simpe.bridge.appmovil.domain.receipt.ReceiptCaptureRecord
import com.simpe.bridge.appmovil.domain.receipt.ReceiptQualityReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.max

class ReceiptCaptureLocalRepository(
    private val context: Context,
    private val dao: ReceiptCaptureDao,
    private val gson: Gson = Gson(),
) {
    fun getHistory(): Flow<List<ReceiptCaptureRecord>> {
        return dao.getAll().map { captures -> captures.map { it.toRecord() } }
    }

    suspend fun saveCapture(
        sourceFile: File,
        report: ReceiptQualityReport,
        optimizedImage: OptimizedReceiptImage?,
        error: String? = null,
    ): String {
        val captureId = UUID.randomUUID().toString()
        val imagePath = optimizedImage?.imageUri?.path ?: sourceFile.absolutePath
        val thumbnailPath = optimizedImage?.thumbnailUri?.path ?: createHistoryThumbnail(sourceFile, captureId)
        dao.upsert(
            ReceiptCaptureEntity(
                captureId = captureId,
                createdAt = System.currentTimeMillis(),
                imagePath = imagePath,
                thumbnailPath = thumbnailPath,
                sha256 = optimizedImage?.sha256,
                score = report.score,
                passed = report.passed,
                uploaded = false,
                uploadMessage = null,
                width = optimizedImage?.width ?: 0,
                height = optimizedImage?.height ?: 0,
                sizeBytes = optimizedImage?.sizeBytes ?: sourceFile.length(),
                metadataJson = gson.toJson(optimizedImage?.metadata),
                checklistJson = gson.toJson(report),
                error = error,
            )
        )
        return captureId
    }

    suspend fun markUploaded(captureId: String, message: String) {
        dao.markUploaded(captureId, message)
    }

    suspend fun hasUploadedHash(hash: String): Boolean {
        return dao.hasUploadedHash(hash)
    }

    private fun createHistoryThumbnail(sourceFile: File, captureId: String): String? {
        return runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(sourceFile.absolutePath, bounds)
            var sample = 1
            while (max(bounds.outWidth, bounds.outHeight) / sample > 360) sample *= 2
            val options = BitmapFactory.Options().apply {
                inSampleSize = sample
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath, options) ?: return null
            val cacheDir = File(context.cacheDir, "receipts").apply { mkdirs() }
            val thumbnail = File(cacheDir, "history_thumb_$captureId.webp")
            FileOutputStream(thumbnail).use { output ->
                bitmap.compress(webpFormat(), 68, output)
            }
            bitmap.recycle()
            thumbnail.absolutePath
        }.getOrNull()
    }

    private fun webpFormat(): Bitmap.CompressFormat {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
    }

    private fun ReceiptCaptureEntity.toRecord(): ReceiptCaptureRecord {
        return ReceiptCaptureRecord(
            captureId = captureId,
            createdAt = createdAt,
            imagePath = imagePath,
            thumbnailPath = thumbnailPath,
            sha256 = sha256,
            score = score,
            passed = passed,
            uploaded = uploaded,
            uploadMessage = uploadMessage,
            width = width,
            height = height,
            sizeBytes = sizeBytes,
            error = error,
        )
    }
}
