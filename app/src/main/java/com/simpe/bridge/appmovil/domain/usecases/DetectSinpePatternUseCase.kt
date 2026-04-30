package com.simpe.bridge.appmovil.domain.usecases

import android.content.Context
import com.simpe.bridge.appmovil.domain.utils.SettingsManager

//Clasificación del mensaje SMS según su origen y contenido.
enum class SmsClassification {
    SINPE,
    NOT_RELEVANT,
    UNKNOWN
}

//Resultado del análisis de detección de patrones SINPE.
data class SinpeDetectionResult(
    val classification: SmsClassification,
    val matchedPattern: SinpePattern?,
    val confidence: Float, // 0.0 a 1.0
    val details: String
)

//Patrones más comunes
enum class SinpePattern(val regex: Regex, val description: String) {
    // Palabra clave simple
    SINPE_KEYWORD(
        Regex("(?i)\\bSINPE\\b", RegexOption.IGNORE_CASE),
        "Palabra SINPE"
    ),
    // Patrones de transferencia recibida
    TRANSFER_RECEIVED(
        Regex("(?i)has recibido|recibió|depósito|deposito|transferencia recibida", RegexOption.IGNORE_CASE),
        "Transferencia recibida"
    ),
    // Patrones de transferencia enviada
    TRANSFER_SENT(
        Regex("(?i)has enviado|envió|transferencia enviada|pago realizado|pago exitoso", RegexOption.IGNORE_CASE),
        "Transferencia enviada"
    ),
    // Monto en colones (formato CRC)
    AMOUNT(
        Regex("₡\\s*[\\d.,]+|[\\d.,]+\\s*(?:colones?|CRC)", RegexOption.IGNORE_CASE),
        "Monto en colones"
    ),
    // Referencia (6-10 dígitos típico)
    REFERENCE(
        Regex("(?i)ref\\.?\\s*\\d+|referencia\\s*\\d+", RegexOption.IGNORE_CASE),
        "Número de referencia"
    )
}

//Detecta y clasifica el sms
class DetectSinpePatternUseCase(private val context: Context? = null) {

    private val settingsManager = context?.let { SettingsManager(it) }
    
   //Minimo de confianza
    private val confidenceThreshold = 0.5f

   //Analiza el mensaje
    operator fun invoke(sender: String, body: String): SinpeDetectionResult {
        // Limpiar inputs
        val cleanSender = sender.trim()
        val cleanBody = body.trim()

        // 1. Verificar remitente conocido (desde Settings)
        val senderCheck = checkKnownSender(cleanSender)
        if (senderCheck != null) {
            return senderCheck
        }
 
        // 2. Buscar patrones en el contenido
        val matchedPatterns = findMatchingPatterns(cleanBody)

        if (matchedPatterns.isEmpty()) {
            return SinpeDetectionResult(
                classification = SmsClassification.NOT_RELEVANT,
                matchedPattern = null,
                confidence = 0.0f,
                details = "No se detectaron patrones SINPE"
            )
        }

        // 3. Calcular confianza
        val confidence = calculateConfidence(matchedPatterns)

        // 4. Clasificar según umbral
        val classification = if (confidence >= confidenceThreshold) {
            SmsClassification.SINPE
        } else {
            SmsClassification.UNKNOWN
        }

        val patternDescriptions = matchedPatterns.joinToString(", ") { it.description }

        return SinpeDetectionResult(
            classification = classification,
            matchedPattern = matchedPatterns.firstOrNull(),
            confidence = confidence,
            details = "Patrones: $patternDescriptions"
        )
    }

   //Verifica si el remitente esta en la lista
    private fun checkKnownSender(sender: String): SinpeDetectionResult? {
        val senders = settingsManager?.getSinpeSenders() ?: SettingsManager.DEFAULT_SENDERS
        
        for (known in senders) {
            if (sender.contains(known, ignoreCase = true)) {
                return SinpeDetectionResult(
                    classification = SmsClassification.SINPE,
                    matchedPattern = SinpePattern.TRANSFER_RECEIVED,
                    confidence = 0.95f,
                    details = "Remitente conocido: $sender"
                )
            }
        }
        
        return null
    }

   
    private fun findMatchingPatterns(body: String): List<SinpePattern> {
        return SinpePattern.entries.filter { pattern ->
            pattern.regex.containsMatchIn(body)
        }
    }

  
    private fun calculateConfidence(matchedPatterns: List<SinpePattern>): Float {
        if (matchedPatterns.isEmpty()) return 0f
        
        var confidence = 0f
        for (pattern in matchedPatterns) {
            confidence += when (pattern) {
                SinpePattern.SINPE_KEYWORD -> 0.8f  // Palabra SINPE es un indicador fuerte
                SinpePattern.TRANSFER_RECEIVED, SinpePattern.TRANSFER_SENT -> 0.4f
                SinpePattern.AMOUNT -> 0.3f
                SinpePattern.REFERENCE -> 0.2f
            }
        }
        
        return confidence.coerceIn(0f, 1f)
    }
}