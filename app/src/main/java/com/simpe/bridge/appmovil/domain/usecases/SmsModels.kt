package com.simpe.bridge.appmovil.domain.usecases

import java.util.UUID

enum class MessageStatus {
    PENDING, SENT, FAILED
}

data class SmsEnvelope(
    val messageId: String = UUID.randomUUID().toString(),
    val correlationId: String = UUID.randomUUID().toString(),
    val createdAt: Long = System.currentTimeMillis(),
    val source: String = "android-sms-listener",
    val version: String = "1.0",
    val contentHash: String,
    val deviceHash: String,
    val signature: String,
    val status: MessageStatus,
    val retryCount: Int = 0,
    val lastAttemptAt: Long? = null
) {
    fun copyWithStatus(newStatus: MessageStatus): SmsEnvelope = this.copy(status = newStatus)
}

data class SmsPayload(
    val sender: String,
    val body: String,
    val timestamp: Long,
    val metadata: SmsMetadata,
    val multipart: SmsMultipart,
    val device: SmsDeviceInfo,
    val debug: SmsDebugInfo
)

data class SmsMetadata(
    val serviceCenter: String?,
    val protocolId: Int,
    val status: Int,
    val isStatusReport: Boolean,
    val isReplyPathPresent: Boolean
)

data class SmsMultipart(
    val ref: Int,
    val seq: Int,
    val total: Int
)

data class SmsDeviceInfo(
    val subscriptionId: Int,
    val simSlot: Int,
    val networkOperator: String?
)

data class SmsDebugInfo(
    val pdu: String,
    val format: String
)

data class SmsMessage(
    val envelope: SmsEnvelope,
    val payload: SmsPayload
)
