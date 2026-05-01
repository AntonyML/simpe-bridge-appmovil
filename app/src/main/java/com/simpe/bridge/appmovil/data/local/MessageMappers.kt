package com.simpe.bridge.appmovil.data.local

import com.simpe.bridge.appmovil.domain.usecases.*

fun MessageEntity.toDomain(): SmsMessage = SmsMessage(
    envelope = SmsEnvelope(
        messageId = messageId,
        correlationId = correlationId,
        createdAt = createdAt,
        source = source,
        version = version,
        contentHash = contentHash,
        deviceHash = deviceHash,
        signature = signature,
        status = status,
        retryCount = retryCount,
        lastAttemptAt = lastAttemptAt,
        classification = SmsClassification.valueOf(classification),
        detectionConfidence = detectionConfidence,
        detectionDetails = detectionDetails
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

fun SmsMessage.toEntity(): MessageEntity = MessageEntity(
    messageId = envelope.messageId,
    correlationId = envelope.correlationId,
    createdAt = envelope.createdAt,
    source = envelope.source,
    version = envelope.version,
    contentHash = envelope.contentHash,
    deviceHash = envelope.deviceHash,
    signature = envelope.signature,
    status = envelope.status,
    retryCount = envelope.retryCount,
    lastAttemptAt = envelope.lastAttemptAt,
    sender = payload.sender,
    body = payload.body,
    timestamp = payload.timestamp,
    serviceCenter = payload.metadata.serviceCenter,
    protocolId = payload.metadata.protocolId,
    smsStatus = payload.metadata.status,
    isStatusReport = payload.metadata.isStatusReport,
    isReplyPathPresent = payload.metadata.isReplyPathPresent,
    multipartRef = payload.multipart.ref,
    multipartSeq = payload.multipart.seq,
    multipartTotal = payload.multipart.total,
    subscriptionId = payload.device.subscriptionId,
    simSlot = payload.device.simSlot,
    networkOperator = payload.device.networkOperator,
    pdu = payload.debug.pdu,
    format = payload.debug.format,
    classification = envelope.classification.name,
    detectionConfidence = envelope.detectionConfidence,
    detectionDetails = envelope.detectionDetails
)
