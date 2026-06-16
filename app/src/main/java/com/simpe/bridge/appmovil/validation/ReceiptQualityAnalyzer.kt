package com.simpe.bridge.appmovil.validation

import android.graphics.Bitmap
import com.simpe.bridge.appmovil.domain.receipt.QualityMetric
import com.simpe.bridge.appmovil.domain.receipt.ReceiptQualityReport
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class ReceiptQualityAnalyzer {

    fun analyze(bitmap: Bitmap): ReceiptQualityReport {
        val sample = bitmap.downscaleForAnalysis()
        val gray = sample.toGrayPixels()
        val width = sample.width
        val height = sample.height

        val laplacianVariance = gray.laplacianVariance(width, height)
        val edgeDensity = gray.edgeDensity(width, height)
        val brightnessValue = gray.average()
        val contrastValue = gray.standardDeviation(brightnessValue)
        val textDensity = gray.textLikeDensity(width, height)
        val frame = gray.edgeFrame(width, height)
        val whiteRatio = gray.whiteRatio()
        val blackRatio = gray.blackRatio()

        if (sample !== bitmap) sample.recycle()

        val blur = laplacianVariance.toMetric(
            key = "blur",
            label = "Imagen enfocada",
            low = 30.0,
            target = 160.0,
            detail = "Varianza Laplaciana ${laplacianVariance.format(1)}",
        )
        val brightness = centeredMetric(
            key = "brightness",
            label = "Buena iluminacion",
            value = brightnessValue,
            minGood = 95.0,
            maxGood = 200.0,
            minAcceptable = 60.0,
            maxAcceptable = 230.0,
            detail = "Brillo medio ${brightnessValue.format(0)}",
        )
        val contrast = contrastValue.toMetric(
            key = "contrast",
            label = "Contraste suficiente",
            low = 18.0,
            target = 48.0,
            detail = "Desviacion ${contrastValue.format(1)}",
        )
        val textVisibility = ((textDensity * 1.4 * 0.7) + (edgeDensity * 0.3)).coerceIn(0.0, 1.0).toPercentMetric(
            key = "textVisibility",
            label = "Texto visible",
            passAt = 25,
            detail = "Densidad textual ${(textDensity * 100).format(0)}%",
        )
        val framing = frame.coverageScore.toPercentMetric(
            key = "framing",
            label = "Comprobante completo",
            passAt = 55,
            detail = "Cobertura ${(frame.coverageScore * 100).format(0)}%",
        )
        val perspective = frame.perspectiveScore.toPercentMetric(
            key = "perspective",
            label = "Perspectiva estable",
            passAt = 30,
            detail = "Inclinacion ${(100 - frame.perspectiveScore * 100).format(0)}%",
        )
        val resolution = resolutionMetric(bitmap.width, bitmap.height)

        val weighted = blur.score * 0.18 +
            brightness.score * 0.12 +
            contrast.score * 0.14 +
            textVisibility.score * 0.24 +
            framing.score * 0.16 +
            perspective.score * 0.10 +
            resolution.score * 0.06

        val emptyFramePenalty = if (whiteRatio > 0.97 || blackRatio > 0.92) 12 else 0
        val perspectivePenalty = if (!perspective.passed) 3 else 0
        val resolutionPenalty = if (!resolution.passed) 3 else 0
        val penalties = emptyFramePenalty + perspectivePenalty + resolutionPenalty
        val score = (weighted.toInt() - penalties).coerceIn(0, 100)

        return ReceiptQualityReport(
            score = score,
            blur = blur,
            brightness = brightness,
            contrast = contrast,
            textVisibility = textVisibility,
            framing = framing,
            perspective = perspective,
            resolution = resolution,
        )
    }

    private fun Bitmap.downscaleForAnalysis(): Bitmap {
        val maxSide = 720
        val longest = max(width, height)
        if (longest <= maxSide) return this
        val scale = maxSide.toFloat() / longest
        return Bitmap.createScaledBitmap(this, (width * scale).toInt(), (height * scale).toInt(), true)
    }

    private fun Bitmap.toGrayPixels(): IntArray {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        return IntArray(pixels.size) { index ->
            val color = pixels[index]
            val r = color shr 16 and 0xff
            val g = color shr 8 and 0xff
            val b = color and 0xff
            ((r * 0.299f) + (g * 0.587f) + (b * 0.114f)).toInt()
        }
    }

    private fun IntArray.laplacianVariance(width: Int, height: Int): Double {
        var sum = 0.0
        var sumSq = 0.0
        var count = 0
        var y = 1
        while (y < height - 1) {
            var x = 1
            while (x < width - 1) {
                val i = y * width + x
                val value = (this[i] * -4) + this[i - 1] + this[i + 1] + this[i - width] + this[i + width]
                sum += value
                sumSq += value * value
                count++
                x += 2
            }
            y += 2
        }
        if (count == 0) return 0.0
        val mean = sum / count
        return (sumSq / count) - (mean * mean)
    }

    private fun IntArray.edgeDensity(width: Int, height: Int): Double {
        var edges = 0
        var count = 0
        var y = 1
        while (y < height - 1) {
            var x = 1
            while (x < width - 1) {
                val i = y * width + x
                val gx = abs(this[i + 1] - this[i - 1])
                val gy = abs(this[i + width] - this[i - width])
                if (gx + gy > 30) edges++
                count++
                x += 2
            }
            y += 2
        }
        return if (count == 0) 0.0 else (edges.toDouble() / count).coerceIn(0.0, 1.0)
    }

    private fun IntArray.textLikeDensity(width: Int, height: Int): Double {
        val block = 16
        var textBlocks = 0
        var totalBlocks = 0
        var y = 0
        while (y < height - block) {
            var x = 0
            while (x < width - block) {
                var transitions = 0
                var last = this[y * width + x] > 128
                var row = 0
                while (row < block) {
                    var col = 1
                    while (col < block) {
                        val current = this[(y + row) * width + x + col] > 128
                        if (current != last) transitions++
                        last = current
                        col += 2
                    }
                    row += 4
                }
                if (transitions in 2..65) textBlocks++
                totalBlocks++
                x += block
            }
            y += block
        }
        return if (totalBlocks == 0) 0.0 else (textBlocks.toDouble() / totalBlocks).coerceIn(0.0, 1.0)
    }

    private fun IntArray.edgeFrame(width: Int, height: Int): FrameScore {
        var minX = width
        var minY = height
        var maxX = 0
        var maxY = 0
        var hits = 0
        var y = 1
        while (y < height - 1) {
            var x = 1
            while (x < width - 1) {
                val i = y * width + x
                val gradient = abs(this[i + 1] - this[i - 1]) + abs(this[i + width] - this[i - width])
                if (gradient > 58) {
                    minX = min(minX, x)
                    minY = min(minY, y)
                    maxX = max(maxX, x)
                    maxY = max(maxY, y)
                    hits++
                }
                x += 3
            }
            y += 3
        }
        if (hits < 80) return FrameScore(0.30, 0.40)
        val boxWidth = (maxX - minX).coerceAtLeast(1)
        val boxHeight = (maxY - minY).coerceAtLeast(1)
        val areaRatio = (boxWidth.toDouble() * boxHeight) / (width.toDouble() * height)
        val marginBalanceX = 1.0 - abs(minX - (width - maxX)).toDouble() / width
        val marginBalanceY = 1.0 - abs(minY - (height - maxY)).toDouble() / height
        val coverage = ((areaRatio / 0.72).coerceIn(0.0, 1.0) * 0.72) +
            (marginBalanceX.coerceIn(0.0, 1.0) * 0.14) +
            (marginBalanceY.coerceIn(0.0, 1.0) * 0.14)
        val aspect = boxWidth.toDouble() / boxHeight
        val perspective = (1.0 - abs(aspect - 0.65) / 1.2).coerceIn(0.0, 1.0)
        return FrameScore(coverage.coerceIn(0.0, 1.0), perspective)
    }

    private fun IntArray.average(): Double = if (isEmpty()) 0.0 else sum().toDouble() / size

    private fun IntArray.standardDeviation(mean: Double): Double {
        if (isEmpty()) return 0.0
        val sumSqDiff = sumOf { value -> (value - mean) * (value - mean) }
        return sqrt(sumSqDiff / size)
    }

    private fun IntArray.whiteRatio(): Double = if (isEmpty()) 0.0 else count { it > 235 }.toDouble() / size

    private fun IntArray.blackRatio(): Double = if (isEmpty()) 0.0 else count { it < 18 }.toDouble() / size

    private fun Double.toMetric(key: String, label: String, low: Double, target: Double, detail: String): QualityMetric {
        val score = (((this - low) / (target - low)) * 100).toInt().coerceIn(0, 100)
        return QualityMetric(key, label, score, score >= 60, detail)
    }

    private fun Double.toPercentMetric(key: String, label: String, passAt: Int, detail: String): QualityMetric {
        val score = (this * 100).toInt().coerceIn(0, 100)
        return QualityMetric(key, label, score, score >= passAt, detail)
    }

    private fun centeredMetric(
        key: String,
        label: String,
        value: Double,
        minGood: Double,
        maxGood: Double,
        minAcceptable: Double,
        maxAcceptable: Double,
        detail: String,
    ): QualityMetric {
        val score = when {
            value in minGood..maxGood -> 100
            value < minAcceptable || value > maxAcceptable -> 25
            value < minGood -> (((value - minAcceptable) / (minGood - minAcceptable)) * 75 + 25).toInt()
            else -> (((maxAcceptable - value) / (maxAcceptable - maxGood)) * 75 + 25).toInt()
        }.coerceIn(0, 100)
        return QualityMetric(key, label, score, score >= 60, detail)
    }

    private fun resolutionMetric(width: Int, height: Int): QualityMetric {
        val minSide = min(width, height)
        val longSide = max(width, height)
        val score = min(100, ((minSide / 720.0) * 55 + (longSide / 1280.0) * 45).toInt())
        return QualityMetric(
            key = "resolution",
            label = "Resolucion util",
            score = score,
            passed = minSide >= 540 && longSide >= 960,
            detail = "${width}x$height",
        )
    }

    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)

    private data class FrameScore(
        val coverageScore: Double,
        val perspectiveScore: Double,
    )
}
