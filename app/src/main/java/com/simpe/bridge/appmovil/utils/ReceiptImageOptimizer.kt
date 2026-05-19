package com.simpe.bridge.appmovil.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.util.Size
import androidx.exifinterface.media.ExifInterface
import com.simpe.bridge.appmovil.domain.receipt.OptimizedReceiptImage
import com.simpe.bridge.appmovil.domain.receipt.ReceiptImageMetadata
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ReceiptImageOptimizer(
    private val context: Context,
) {
    fun optimize(source: File): OptimizedReceiptImage {
        val originalSize = readBounds(source)
        val bitmap = decodeSampledBitmap(source, maxDimension = 2400)
        val oriented = bitmap.applyExifOrientation(source)
        if (oriented !== bitmap) bitmap.recycle()

        val cropped = oriented.autoCropReceipt()
        if (cropped !== oriented) oriented.recycle()

        val resized = cropped.resizeToMaxWidth(MAX_WIDTH)
        if (resized !== cropped) cropped.recycle()

        val cacheDir = File(context.cacheDir, "receipts").apply { mkdirs() }
        val imageFile = File(cacheDir, "receipt_${System.currentTimeMillis()}.webp")
        val quality = chooseQuality(resized.width, resized.height)
        FileOutputStream(imageFile).use { output ->
            resized.compress(webpFormat(), quality, output)
        }

        val thumbnail = resized.resizeToMaxWidth(THUMBNAIL_WIDTH)
        val thumbnailFile = File(cacheDir, "receipt_thumb_${System.currentTimeMillis()}.webp")
        FileOutputStream(thumbnailFile).use { output ->
            thumbnail.compress(webpFormat(), THUMBNAIL_QUALITY, output)
        }
        if (thumbnail !== resized) thumbnail.recycle()

        val hash = imageFile.sha256()
        val result = OptimizedReceiptImage(
            imageUri = android.net.Uri.fromFile(imageFile),
            thumbnailUri = android.net.Uri.fromFile(thumbnailFile),
            sha256 = hash,
            width = resized.width,
            height = resized.height,
            sizeBytes = imageFile.length(),
            metadata = ReceiptImageMetadata(
                originalWidth = originalSize.width,
                originalHeight = originalSize.height,
                processedWidth = resized.width,
                processedHeight = resized.height,
                quality = quality,
            ),
        )
        resized.recycle()
        return result
    }

    private fun readBounds(file: File): Size {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        return Size(options.outWidth, options.outHeight)
    }

    private fun decodeSampledBitmap(file: File, maxDimension: Int): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        var sample = 1
        var width = bounds.outWidth
        var height = bounds.outHeight
        while (max(width, height) / sample > maxDimension) sample *= 2
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return requireNotNull(BitmapFactory.decodeFile(file.absolutePath, options)) {
            "No se pudo decodificar la captura"
        }
    }

    private fun Bitmap.applyExifOrientation(file: File): Bitmap {
        val orientation = ExifInterface(file.absolutePath).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL,
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            else -> return this
        }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun Bitmap.autoCropReceipt(): Bitmap {
        val sample = resizeToMaxWidth(700)
        val bounds = sample.detectForegroundBounds()
        val scaleX = width.toFloat() / sample.width
        val scaleY = height.toFloat() / sample.height
        if (sample !== this) sample.recycle()

        val left = (bounds.left * scaleX).toInt().coerceIn(0, width - 1)
        val top = (bounds.top * scaleY).toInt().coerceIn(0, height - 1)
        val right = (bounds.right * scaleX).toInt().coerceIn(left + 1, width)
        val bottom = (bounds.bottom * scaleY).toInt().coerceIn(top + 1, height)
        val cropWidth = right - left
        val cropHeight = bottom - top
        val areaRatio = cropWidth.toDouble() * cropHeight / (width.toDouble() * height)

        if (areaRatio < 0.35 || areaRatio > 0.96) return this
        return Bitmap.createBitmap(this, left, top, cropWidth, cropHeight)
    }

    private fun Bitmap.detectForegroundBounds(): Bounds {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        var minX = width
        var minY = height
        var maxX = 0
        var maxY = 0
        var hits = 0
        for (y in 1 until height - 1 step 2) {
            for (x in 1 until width - 1 step 2) {
                val i = y * width + x
                val center = pixels[i].luma()
                val gx = abs(pixels[i + 1].luma() - pixels[i - 1].luma())
                val gy = abs(pixels[i + width].luma() - pixels[i - width].luma())
                if (gx + gy > 48 || center < 95) {
                    minX = min(minX, x)
                    minY = min(minY, y)
                    maxX = max(maxX, x)
                    maxY = max(maxY, y)
                    hits++
                }
            }
        }
        if (hits < 60) return Bounds(0, 0, width, height)
        val padX = (width * 0.035f).toInt()
        val padY = (height * 0.035f).toInt()
        return Bounds(
            left = (minX - padX).coerceAtLeast(0),
            top = (minY - padY).coerceAtLeast(0),
            right = (maxX + padX).coerceAtMost(width),
            bottom = (maxY + padY).coerceAtMost(height),
        )
    }

    private fun Bitmap.resizeToMaxWidth(maxWidth: Int): Bitmap {
        if (width <= maxWidth) return this
        val scale = maxWidth.toFloat() / width
        return Bitmap.createScaledBitmap(this, maxWidth, (height * scale).toInt(), true)
    }

    private fun Int.luma(): Int {
        val r = this shr 16 and 0xff
        val g = this shr 8 and 0xff
        val b = this and 0xff
        return ((r * 0.299f) + (g * 0.587f) + (b * 0.114f)).toInt()
    }

    private fun chooseQuality(width: Int, height: Int): Int {
        val pixels = width * height
        return when {
            pixels > 1_800_000 -> 78
            pixels > 1_300_000 -> 80
            else -> 84
        }
    }

    private fun webpFormat(): Bitmap.CompressFormat {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
    }

    private fun File.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(this).use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read <= 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private data class Bounds(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
    )

    companion object {
        private const val MAX_WIDTH = 1400
        private const val THUMBNAIL_WIDTH = 320
        private const val THUMBNAIL_QUALITY = 68
    }
}
