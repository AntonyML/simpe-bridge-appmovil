package com.simpe.bridge.appmovil.domain.usecases

import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessages(): Flow<List<SmsMessage>>
    suspend fun saveMessage(message: SmsMessage)
}
