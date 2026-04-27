package com.simpe.bridge.appmovil.data.local

import com.simpe.bridge.appmovil.domain.usecases.SmsMessage

fun MessageEntity.toDomain(): SmsMessage = SmsMessage(
    id = id,
    sender = sender,
    content = content,
    timestamp = timestamp,
)

fun SmsMessage.toEntity(): MessageEntity = MessageEntity(
    id = id,
    sender = sender,
    content = content,
    timestamp = timestamp,
)
