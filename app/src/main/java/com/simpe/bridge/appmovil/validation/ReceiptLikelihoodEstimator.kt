package com.simpe.bridge.appmovil.validation

import com.simpe.bridge.appmovil.domain.receipt.ReceiptLikelihoodReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptLikelihoodSignal
import com.simpe.bridge.appmovil.domain.receipt.ReceiptQualityReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptSemanticReport

class ReceiptLikelihoodEstimator {

    fun estimate(
        visual: ReceiptQualityReport,
        semantic: ReceiptSemanticReport,
    ): ReceiptLikelihoodReport {
        val signals = mutableListOf<ReceiptLikelihoodSignal>()

        val topKeyword = semantic.keywordHits.maxByOrNull { it.weight * it.occurrences }
        if (topKeyword != null) {
            val contribution = (topKeyword.weight * minOf(topKeyword.occurrences, 2)).coerceAtMost(40)
            signals.add(
                ReceiptLikelihoodSignal(
                    key = "keyword_top",
                    label = "Coincidencia fuerte",
                    weight = 40,
                    contribution = contribution,
                    passed = contribution >= 18,
                    detail = "${topKeyword.keyword} x${topKeyword.occurrences}",
                )
            )
        } else {
            signals.add(
                ReceiptLikelihoodSignal(
                    key = "keyword_top",
                    label = "Coincidencia fuerte",
                    weight = 40,
                    contribution = 0,
                    passed = false,
                    detail = "Sin palabras clave",
                )
            )
        }

        val structureBonus = (semantic.structureHits.size * 12).coerceAtMost(36)
        signals.add(
            ReceiptLikelihoodSignal(
                key = "structure",
                label = "Estructura documental",
                weight = 36,
                contribution = structureBonus,
                passed = structureBonus >= 18,
                detail = "${semantic.structureHits.size} secciones",
            )
        )

        val densityContribution = (semantic.score * 0.20).toInt().coerceIn(0, 20)
        signals.add(
            ReceiptLikelihoodSignal(
                key = "density",
                label = "Densidad de texto",
                weight = 20,
                contribution = densityContribution,
                passed = densityContribution >= 10,
                detail = "${semantic.characterCount} caracteres",
            )
        )

        val visualContribution = ((visual.score - 70).coerceAtLeast(0) * 0.20).toInt().coerceIn(0, 12)
        signals.add(
            ReceiptLikelihoodSignal(
                key = "visual_support",
                label = "Apoyo visual",
                weight = 12,
                contribution = visualContribution,
                passed = visualContribution >= 6,
                detail = "Score visual ${visual.score}",
            )
        )

        val screenshotPenalty = semantic.screenshotSignals.size * 6
        signals.add(
            ReceiptLikelihoodSignal(
                key = "anti_screenshot",
                label = "Anti captura",
                weight = 12,
                contribution = 12 - screenshotPenalty.coerceAtMost(12),
                passed = screenshotPenalty == 0,
                detail = if (screenshotPenalty == 0) "Sin senales UI" else "${semantic.screenshotSignals.size} senales",
            )
        )

        val total = signals.sumOf { it.contribution }
        val score = total.coerceIn(0, 100)
        val passed = score >= ReceiptLikelihoodReport.PASS_THRESHOLD &&
            semantic.keywordHits.isNotEmpty() &&
            semantic.screenshotSignals.size < 3

        return ReceiptLikelihoodReport(
            score = score,
            signals = signals,
            passed = passed,
        )
    }
}
