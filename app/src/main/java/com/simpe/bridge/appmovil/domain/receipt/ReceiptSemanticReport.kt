package com.simpe.bridge.appmovil.domain.receipt

data class ReceiptSemanticReport(
    val score: Int,
    val ocrText: String,
    val normalizedText: String,
    val lineCount: Int,
    val blockCount: Int,
    val characterCount: Int,
    val wordCount: Int,
    val keywordHits: List<ReceiptKeywordHit>,
    val structureHits: List<String>,
    val screenshotSignals: List<String>,
    val passed: Boolean,
) {
    companion object {
        const val PASS_THRESHOLD = 55
    }
}

data class ReceiptKeywordHit(
    val keyword: String,
    val weight: Int,
    val occurrences: Int,
)

data class ReceiptLikelihoodReport(
    val score: Int,
    val signals: List<ReceiptLikelihoodSignal>,
    val passed: Boolean,
) {
    companion object {
        const val PASS_THRESHOLD = 55
    }
}

data class ReceiptLikelihoodSignal(
    val key: String,
    val label: String,
    val weight: Int,
    val contribution: Int,
    val passed: Boolean,
    val detail: String,
)

data class ReceiptFinalReport(
    val visualScore: Int,
    val semanticScore: Int,
    val likelihoodScore: Int,
    val finalScore: Int,
    val passed: Boolean,
    val reasons: List<String>,
) {
    companion object {
        const val PASS_THRESHOLD = 60
    }
}
