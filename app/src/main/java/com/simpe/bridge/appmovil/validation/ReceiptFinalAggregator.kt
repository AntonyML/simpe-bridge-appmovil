package com.simpe.bridge.appmovil.validation

import com.simpe.bridge.appmovil.domain.receipt.ReceiptFinalReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptLikelihoodReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptQualityReport
import com.simpe.bridge.appmovil.domain.receipt.ReceiptSemanticReport

class ReceiptFinalAggregator {

    fun aggregate(
        visual: ReceiptQualityReport,
        semantic: ReceiptSemanticReport,
        likelihood: ReceiptLikelihoodReport,
    ): ReceiptFinalReport {
        val weighted = visual.score * 0.18 +
            semantic.score * 0.50 +
            likelihood.score * 0.32
        val finalScore = weighted.toInt().coerceIn(0, 100)

        val strongContent = semantic.passed &&
            semantic.keywordHits.isNotEmpty() &&
            semantic.score >= 65 &&
            likelihood.passed

        val reasons = mutableListOf<String>()
        if (!visual.passed && !strongContent) {
            reasons.add("Calidad visual insuficiente (${visual.score})")
        }
        if (!semantic.passed) {
            reasons += semanticReasons(semantic)
        }
        if (!likelihood.passed) {
            reasons += likelihoodReasons(likelihood)
        }
        if (semantic.screenshotSignals.isNotEmpty() && !strongContent) {
            reasons += "Captura con senales de UI o aplicacion"
        }
        if (semantic.keywordHits.isEmpty()) {
            reasons += "Sin palabras clave reconocibles de SINPE"
        }
        if (visual.framing.passed.not() && !strongContent) {
            reasons += "Encuadre incompleto del comprobante"
        }
        if (visual.resolution.passed.not() && !strongContent) {
            reasons += "Resolucion insuficiente para lectura"
        }

        val passed = finalScore >= ReceiptFinalReport.PASS_THRESHOLD &&
            reasons.isEmpty()

        return ReceiptFinalReport(
            visualScore = visual.score,
            semanticScore = semantic.score,
            likelihoodScore = likelihood.score,
            finalScore = finalScore,
            passed = passed,
            reasons = reasons.distinct(),
        )
    }

    private fun semanticReasons(semantic: ReceiptSemanticReport): List<String> {
        val list = mutableListOf<String>()
        if (semantic.characterCount < 24) list += "Muy poco texto detectado (${semantic.characterCount} caracteres)"
        if (semantic.lineCount < 2) list += "Pocas lineas detectadas (${semantic.lineCount})"
        if (semantic.keywordHits.isEmpty()) list += "Sin coincidencias de palabras SINPE"
        if (list.isEmpty()) list += "Texto no parece un comprobante SINPE (${semantic.score})"
        return list
    }

    private fun likelihoodReasons(likelihood: ReceiptLikelihoodReport): List<String> {
        val list = mutableListOf<String>()
        val failed = likelihood.signals.filter { !it.passed }
        for (signal in failed) {
            list += "${signal.label}: ${signal.detail}"
        }
        if (list.isEmpty()) list += "Probabilidad baja de comprobante (${likelihood.score})"
        return list
    }
}
