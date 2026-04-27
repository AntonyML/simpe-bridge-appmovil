package com.simpe.bridge.appmovil.domain.usecases

data class SmsMessage(
    val id: Long = 0,
    val sender: String,
    val content: String,
    val timestamp: Long,
)
