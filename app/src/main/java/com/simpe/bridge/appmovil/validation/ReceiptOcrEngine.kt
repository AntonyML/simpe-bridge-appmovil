package com.simpe.bridge.appmovil.validation

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.simpe.bridge.appmovil.domain.receipt.ReceiptKeywordHit
import com.simpe.bridge.appmovil.domain.receipt.ReceiptSemanticReport
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ReceiptOcrEngine {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    suspend fun recognize(bitmap: Bitmap): OcrExtraction {
        val input = InputImage.fromBitmap(bitmap, 0)
        return suspendCancellableCoroutine { cont ->
            recognizer.process(input)
                .addOnSuccessListener { result ->
                    val rawText = result.text
                    val blocks = result.textBlocks.size
                    val lines = result.textBlocks.sumOf { it.lines.size }
                    cont.resume(
                        OcrExtraction(
                            rawText = rawText,
                            blockCount = blocks,
                            lineCount = lines,
                        )
                    )
                }
                .addOnFailureListener { error -> cont.resumeWithException(error) }
        }
    }

    fun close() {
        runCatching { recognizer.close() }
    }

    fun buildReport(extraction: OcrExtraction): ReceiptSemanticReport {
        val raw = extraction.rawText
        val normalized = normalize(raw)
        val wordCount = normalized.split(Regex("\\s+")).count { it.isNotBlank() }
        val characterCount = normalized.count { !it.isWhitespace() }
        val hits = keywordHits(normalized)
        val structure = structureHits(normalized)
        val screenshotSignals = screenshotSignals(normalized, extraction)
        val densityScore = densityScore(
            characters = characterCount,
            lines = extraction.lineCount,
            blocks = extraction.blockCount,
        )
        val keywordScore = keywordWeightedScore(hits)
        val structureScore = structureScore(structure)
        val antiScreenshot = antiScreenshotScore(screenshotSignals, densityScore)
        val penalty = emptyOrUselessTextPenalty(characterCount, extraction.lineCount, hits)

        val weighted = keywordScore * 0.45 +
            structureScore * 0.25 +
            densityScore * 0.20 +
            antiScreenshot * 0.10
        val score = (weighted.toInt() - penalty).coerceIn(0, 100)
        val passed = score >= ReceiptSemanticReport.PASS_THRESHOLD &&
            hits.isNotEmpty() &&
            characterCount >= MIN_CHARS
        return ReceiptSemanticReport(
            score = score,
            ocrText = raw,
            normalizedText = normalized,
            lineCount = extraction.lineCount,
            blockCount = extraction.blockCount,
            characterCount = characterCount,
            wordCount = wordCount,
            keywordHits = hits,
            structureHits = structure,
            screenshotSignals = screenshotSignals,
            passed = passed,
        )
    }

    private fun normalize(text: String): String {
        return text
            .lowercase()
            .replace('á', 'a').replace('é', 'e').replace('í', 'i')
            .replace('ó', 'o').replace('ú', 'u').replace('ñ', 'n')
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun keywordHits(normalized: String): List<ReceiptKeywordHit> {
        val matches = mutableListOf<ReceiptKeywordHit>()
        for (entry in KEYWORDS) {
            val occurrences = countOccurrences(normalized, entry.keyword)
            if (occurrences > 0) {
                matches.add(
                    ReceiptKeywordHit(
                        keyword = entry.keyword,
                        weight = entry.weight,
                        occurrences = occurrences,
                    )
                )
            }
        }
        return matches
    }

    private fun countOccurrences(text: String, keyword: String): Int {
        if (keyword.isBlank()) return 0
        var idx = 0
        var count = 0
        while (true) {
            val found = text.indexOf(keyword, idx)
            if (found < 0) break
            count++
            idx = found + keyword.length
        }
        return count
    }

    private fun keywordWeightedScore(hits: List<ReceiptKeywordHit>): Double {
        if (hits.isEmpty()) return 0.0
        val total = hits.sumOf { it.weight * minOf(it.occurrences, 2) }
        return ((total.toDouble() / KEYWORD_CEILING) * 100.0).coerceIn(0.0, 100.0)
    }

    private fun structureHits(text: String): List<String> {
        val found = mutableListOf<String>()
        for (entry in STRUCTURE_LABELS) {
            if (text.contains(entry.pattern)) {
                found.add(entry.label)
            }
        }
        return found
    }

    private fun structureScore(structure: List<String>): Double {
        if (structure.isEmpty()) return 0.0
        val maxScore = STRUCTURE_LABELS.size.toDouble()
        val ratio = structure.size / maxScore
        val bonus = minOf(structure.size, 4) * 6.0
        return ((ratio * 70.0) + bonus).coerceIn(0.0, 100.0)
    }

    private fun densityScore(characters: Int, lines: Int, blocks: Int): Double {
        if (characters < MIN_CHARS) return (characters.toDouble() / MIN_CHARS) * 25.0
        val charScore = (characters.toDouble() / IDEAL_CHARS).coerceIn(0.0, 1.0) * 60.0
        val lineScore = (lines.toDouble() / IDEAL_LINES).coerceIn(0.0, 1.0) * 25.0
        val blockScore = (blocks.toDouble() / IDEAL_BLOCKS).coerceIn(0.0, 1.0) * 15.0
        return (charScore + lineScore + blockScore).coerceIn(0.0, 100.0)
    }

    private fun screenshotSignals(text: String, extraction: OcrExtraction): List<String> {
        val signals = mutableListOf<String>()
        for (entry in SCREENSHOT_TOKENS) {
            if (text.contains(entry)) signals.add(entry)
        }
        if (extraction.blockCount >= 30 && extraction.lineCount >= 90) {
            signals.add("ui:bloques_densos")
        }
        if (extraction.lineCount >= 120) {
            signals.add("ui:lineas_densas")
        }
        return signals
    }

    private fun antiScreenshotScore(signals: List<String>, densityScore: Double): Double {
        if (signals.isEmpty()) return densityScore.coerceIn(0.0, 100.0)
        val penalty = signals.size * 8.0
        return (densityScore - penalty).coerceIn(0.0, 100.0)
    }

    private fun emptyOrUselessTextPenalty(characters: Int, lines: Int, hits: List<ReceiptKeywordHit>): Int {
        var penalty = 0
        if (characters < MIN_CHARS) penalty += 30
        if (lines < MIN_LINES) penalty += 18
        if (hits.isEmpty()) penalty += 20
        return penalty
    }

    data class OcrExtraction(
        val rawText: String,
        val blockCount: Int,
        val lineCount: Int,
    )

    private companion object {
        const val MIN_CHARS = 24
        const val MIN_LINES = 2
        const val IDEAL_CHARS = 180.0
        const val IDEAL_LINES = 8.0
        const val IDEAL_BLOCKS = 4.0
        const val KEYWORD_CEILING = 220.0

        data class Keyword(val keyword: String, val weight: Int)

        val KEYWORDS = listOf(
            Keyword("sinpe movil", 32),
            Keyword("sinpe", 26),
            Keyword("comprobante", 18),
            Keyword("transferencia", 16),
            Keyword("transaccion", 14),
            Keyword("autorizacion", 14),
            Keyword("confirmacion", 12),
            Keyword("referencia", 16),
            Keyword("banco", 10),
            Keyword("bncr", 18),
            Keyword("bcr", 18),
            Keyword("bac", 18),
            Keyword("popular", 14),
            Keyword("scotiabank", 14),
            Keyword("davivienda", 12),
            Keyword("imsa", 12),
            Keyword("coopenae", 12),
            Keyword("mutual", 10),
            Keyword("lafise", 12),
            Keyword("banco de costa rica", 14),
            Keyword("banco nacional", 14),
            Keyword("iban", 8),
            Keyword("monto", 10),
            Keyword("importe", 10),
            Keyword("total", 6),
            Keyword("pago", 8),
            Keyword("movil", 8),
            Keyword("destinatario", 10),
            Keyword("destino", 8),
            Keyword("origen", 6),
            Keyword("cuenta", 10),
            Keyword("fecha", 6),
            Keyword("hora", 4),
            Keyword("exitosa", 10),
            Keyword("procesado", 10),
            Keyword("aprobada", 10),
            Keyword("moneda", 6),
            Keyword("colones", 6),
            Keyword("dolares", 6),
            Keyword("crc", 6),
            Keyword("usd", 6),
            Keyword("saldo", 4),
            Keyword("cajero", 4),
        )

        data class StructureLabel(val pattern: String, val label: String)

        val STRUCTURE_LABELS = listOf(
            StructureLabel("monto", "Monto"),
            StructureLabel("importe", "Importe"),
            StructureLabel("total", "Total"),
            StructureLabel("fecha", "Fecha"),
            StructureLabel("hora", "Hora"),
            StructureLabel("referencia", "Referencia"),
            StructureLabel("banco", "Banco"),
            StructureLabel("cuenta", "Cuenta"),
            StructureLabel("destino", "Destino"),
            StructureLabel("origen", "Origen"),
            StructureLabel("autorizacion", "Autorizacion"),
            StructureLabel("transaccion", "Transaccion"),
            StructureLabel("comprobante", "Comprobante"),
            StructureLabel("confirmacion", "Confirmacion"),
        )

        val SCREENSHOT_TOKENS = listOf(
            "whatsapp",
            "telegram",
            "facebook",
            "instagram",
            "tiktok",
            "twitter",
            "youtube",
            "snapchat",
            "conversacion",
            "reproductor",
            "captura de pantalla",
            "esquema",
        )
    }
}
