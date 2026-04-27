package com.simpe.bridge.appmovil.data.remote

import com.google.gson.annotations.SerializedName
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage

data class SmsDto(
    @SerializedName("envelope") val envelope: EnvelopeDto,
    @SerializedName("payload") val payload: PayloadDto
)

data class EnvelopeDto(
    @SerializedName("message_id") val messageId: String,
    @SerializedName("correlation_id") val correlationId: String,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("source") val source: String,
    @SerializedName("version") val version: String,
    @SerializedName("content_hash") val contentHash: String,
    @SerializedName("device_hash") val deviceHash: String,
    @SerializedName("signature") val signature: String,
    @SerializedName("status") val status: String,
    @SerializedName("retry_count") val retryCount: Int
)

data class PayloadDto(
    @SerializedName("sender") val sender: String,
    @SerializedName("body") val body: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("metadata") val metadata: MetadataDto,
    @SerializedName("multipart") val multipart: MultipartDto,
    @SerializedName("device") val device: DeviceDto,
    @SerializedName("debug") val debug: DebugDto
)

data class MetadataDto(
    @SerializedName("service_center") val serviceCenter: String?,
    @SerializedName("protocol_id") val protocolId: Int,
    @SerializedName("status") val status: Int
)

data class MultipartDto(
    @SerializedName("ref") val ref: Int,
    @SerializedName("seq") val seq: Int,
    @SerializedName("total") val total: Int
)

data class DeviceDto(
    @SerializedName("subscription_id") val subscriptionId: Int,
    @SerializedName("sim_slot") val simSlot: Int,
    @SerializedName("network_operator") val networkOperator: String?
)

data class DebugDto(
    @SerializedName("pdu") val pdu: String,
    @SerializedName("format") val format: String
)

fun SmsMessage.toDto(): SmsDto = SmsDto(
    envelope = EnvelopeDto(
        messageId = envelope.messageId,
        correlationId = envelope.correlationId,
        createdAt = envelope.createdAt / 1000, // API usually expects seconds or consistent epoch
        source = envelope.source,
        version = envelope.version,
        contentHash = envelope.contentHash,
        deviceHash = envelope.deviceHash,
        signature = envelope.signature,
        status = envelope.status.name,
        retryCount = envelope.retryCount
    ),
    payload = PayloadDto(
        sender = payload.sender,
        body = payload.body,
        timestamp = payload.timestamp / 1000,
        metadata = MetadataDto(
            serviceCenter = payload.metadata.serviceCenter,
            protocolId = payload.metadata.protocolId,
            status = payload.metadata.status
        ),
        multipart = MultipartDto(
            ref = payload.multipart.ref,
            seq = payload.multipart.seq,
            total = payload.multipart.total
        ),
        device = DeviceDto(
            subscriptionId = payload.device.subscriptionId,
            simSlot = payload.device.simSlot,
            networkOperator = payload.device.networkOperator
        ),
        debug = DebugDto(
            pdu = payload.debug.pdu,
            format = payload.debug.format
        )
    )
)
