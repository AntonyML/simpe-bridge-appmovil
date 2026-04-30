package com.simpe.bridge.appmovil.domain.usecases

import android.content.Context
import com.simpe.bridge.appmovil.domain.utils.SecurityUtils

class ProcessSmsUseCase(private val context: Context) {
    
    operator fun invoke(
        sender: String,
        body: String, 
        timestamp: Long,
        serviceCenter: String?,
        protocolId: Int,
        smsStatus: Int,
        isStatusReport: Boolean,
        isReplyPathPresent: Boolean,
        multipartRef: Int,
        multipartSeq: Int,
        multipartTotal: Int,
        subscriptionId: Int,
        simSlot: Int,
        networkOperator: String?,
        pdu: String,
        format: String,
        messageStatus: MessageStatus = MessageStatus.PENDING,
        detectionResult: SinpeDetectionResult? = null
    ): SmsMessage {
        
        val contentHash = SecurityUtils.generateSHA256("$sender|$body|$timestamp")
        val deviceHash = SecurityUtils.getDeviceHash(context)
        
        // Placeholder for a real secret management
        val secret = "SECRET_KEY_PLACEHOLDER"
        val signature = SecurityUtils.generateHMAC("$contentHash|$deviceHash", secret)
        
        // Extract classification from detection result
        val classification = detectionResult?.classification ?: SmsClassification.UNKNOWN
        val confidence = detectionResult?.confidence ?: 0f
        val details = detectionResult?.details ?: ""
        
        return SmsMessage(
            envelope = SmsEnvelope(
                contentHash = contentHash,
                deviceHash = deviceHash,
                signature = signature,
                status = messageStatus,
                classification = classification,
                detectionConfidence = confidence,
                detectionDetails = details
            ),
            payload = SmsPayload(
                sender = sender,
                body = body,
                timestamp = timestamp,
                metadata = SmsMetadata(
                    serviceCenter = serviceCenter,
                    protocolId = protocolId,
                    status = smsStatus,
                    isStatusReport = isStatusReport,
                    isReplyPathPresent = isReplyPathPresent
                ),
                multipart = SmsMultipart(
                    ref = multipartRef,
                    seq = multipartSeq,
                    total = multipartTotal
                ),
                device = SmsDeviceInfo(
                    subscriptionId = subscriptionId,
                    simSlot = simSlot,
                    networkOperator = networkOperator
                ),
                debug = SmsDebugInfo(
                    pdu = pdu,
                    format = format
                )
            )
        )
    }
}
