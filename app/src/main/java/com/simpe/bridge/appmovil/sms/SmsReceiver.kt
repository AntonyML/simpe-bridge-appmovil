package com.simpe.bridge.appmovil.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.simpe.bridge.appmovil.data.local.AppDatabase
import com.simpe.bridge.appmovil.data.repository.MessageRepositoryImpl
import com.simpe.bridge.appmovil.domain.usecases.*
import com.simpe.bridge.appmovil.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION &&
            intent.action != Telephony.Sms.Intents.SMS_DELIVER_ACTION
        ) {
            return
        }

        val pendingResult = goAsync()
        var broadcastFinished = false

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                // 1. Capture SMS data
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                if (messages.isEmpty()) {
                    android.util.Log.w("SmsReceiver", "FILTRO: SMS no contiene mensajes")
                    broadcastFinished = true
                    return@launch
                }

                val firstSms = messages.firstOrNull() ?: throw IllegalStateException("Empty message list")
                val sender = firstSms.displayOriginatingAddress.orEmpty()
                val content = messages.joinToString(separator = "") { it.messageBody.orEmpty() }
                val timestamp = firstSms.timestampMillis

                android.util.Log.d("SmsReceiver", " SMS RECIBIDO => Remitente: '$sender' | Contenido: '$content'")

                // Shared repository and DAO
                val dao = AppDatabase.getInstance(context.applicationContext).messageDao()
                val repository = MessageRepositoryImpl(dao, context.applicationContext)
                val saveMessageUseCase = SaveMessageUseCase(repository)
                val processSms = ProcessSmsUseCase(context)
                val validateSms = ValidateSmsUseCase()
                val detectSinpe = DetectSinpePatternUseCase(context)

                // State determination
                var finalStatus = MessageStatus.SENT

                // 2. Validate SMS
                if (!validateSms(sender, content)) {
                    android.util.Log.w("SmsReceiver", "FILTRO: Validacion basica fallo")
                    finalStatus = MessageStatus.FAILED
                    broadcastFinished = true
                    return@launch
                } else {
                    android.util.Log.d("SmsReceiver", "VALIDACION OK: SMS paso validacion basica")
                }

                // 2.5. Detect SINPE patterns
                val detectionResult = detectSinpe(sender, content)
                
                android.util.Log.d("SmsReceiver", " DETECCIÓN SINPE:")
                android.util.Log.d("SmsReceiver", "   • Clasificación: ${detectionResult.classification}")
                android.util.Log.d("SmsReceiver", "   • Confianza: ${detectionResult.confidence}")
                android.util.Log.d("SmsReceiver", "   • Detalles: ${detectionResult.details}")
                android.util.Log.d("SmsReceiver", "   • Patrón coincidido: ${detectionResult.matchedPattern}")

                // Only process and notify SINPE messages
                val isSinpeMessage = detectionResult.classification == SmsClassification.SINPE

                if (!isSinpeMessage) {
                    android.util.Log.w("SmsReceiver", "FILTRO BLOQUEADO: NO es SINPE - Clasificacion: ${detectionResult.classification}")
                    android.util.Log.w("SmsReceiver", "   Remitente: $sender | Contenido: $content")
                    android.util.Log.w("SmsReceiver", "   Razon: ${detectionResult.details}")
                    broadcastFinished = true
                    return@launch
                }

                android.util.Log.d("SmsReceiver", " ACEPTADO: Mensaje clasificado como SINPE - Procesando...")

                try {
                    // 3. Process SMS (Generates IDs, hashes, signatures)
                    // If validation failed above, we still process it but with FAILED status for traceability
                    val robustMessage = processSms(
                        sender = sender,
                        body = content,
                        timestamp = timestamp,
                        serviceCenter = firstSms.serviceCenterAddress,
                        protocolId = firstSms.protocolIdentifier,
                        smsStatus = firstSms.status,
                        isStatusReport = firstSms.isStatusReportMessage,
                        isReplyPathPresent = firstSms.isReplyPathPresent,
                        multipartRef = 0,
                        multipartSeq = 1,
                        multipartTotal = messages.size,
                        subscriptionId = intent.getIntExtra("subscription", -1),
                        simSlot = intent.getIntExtra("slot", -1),
                        networkOperator = null,
                        pdu = firstSms.pdu?.joinToString("") { "%02x".format(it) }.orEmpty(),
                        format = intent.getStringExtra("format") ?: "unknown",
                        messageStatus = finalStatus,
                        detectionResult = detectionResult
                    )

                    // 4. Final Persistence
                    saveMessageUseCase(robustMessage)
                    android.util.Log.d("SmsReceiver", "OK GUARDADO: Mensaje SINPE persistido")
                    
                    // Trigger notification
                    if (finalStatus == MessageStatus.SENT) {
                        NotificationHelper.showSmsNotification(context, sender, content)
                        android.util.Log.d("SmsReceiver", "NOTIFICACION: Enviada al usuario")
                    }
                    broadcastFinished = true

                } catch (processingException: Exception) {
                    android.util.Log.e("SmsReceiver", "ERROR: Excepcion durante procesamiento", processingException)
                    // Fail-safe persistence for unexpected processing errors
                    try {
                        val failedMessage = processSms(
                            sender = sender,
                            body = content,
                            timestamp = timestamp,
                            serviceCenter = firstSms.serviceCenterAddress,
                            protocolId = firstSms.protocolIdentifier,
                            smsStatus = firstSms.status,
                            isStatusReport = firstSms.isStatusReportMessage,
                            isReplyPathPresent = firstSms.isReplyPathPresent,
                            multipartRef = 0,
                            multipartSeq = 1,
                            multipartTotal = messages.size,
                            subscriptionId = intent.getIntExtra("subscription", -1),
                            simSlot = intent.getIntExtra("slot", -1),
                            networkOperator = null,
                            pdu = firstSms.pdu?.joinToString("") { "%02x".format(it) }.orEmpty(),
                            format = intent.getStringExtra("format") ?: "unknown",
                            messageStatus = MessageStatus.FAILED,
                            detectionResult = detectionResult
                        )
                        saveMessageUseCase(failedMessage)
                        android.util.Log.w("SmsReceiver", "RECUPERACION: Mensaje guardado como FAILED")
                    } catch (e: Exception) {
                        android.util.Log.e("SmsReceiver", "ERROR: Recuperacion fallo", e)
                    }
                    processingException.printStackTrace()
                    broadcastFinished = true
                }

            } catch (e: Exception) {
                // Critical system failure
                android.util.Log.e("SmsReceiver", "ERROR CRITICO: Fallo del sistema", e)
                e.printStackTrace()
                broadcastFinished = true
            } finally {
                // Call finish() ONLY ONCE at the very end
                try {
                    if (!broadcastFinished) {
                        android.util.Log.w("SmsReceiver", "WARNING: Finalizando sin procesar")
                    }
                    pendingResult.finish()
                    android.util.Log.d("SmsReceiver", "OK: BroadcastReceiver finalizado correctamente")
                } catch (e: Exception) {
                    android.util.Log.e("SmsReceiver", "ERROR: Fallo al finalizar broadcast", e)
                }
            }
        }
    }
}
