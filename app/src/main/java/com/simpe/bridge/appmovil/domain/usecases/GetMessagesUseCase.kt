package com.simpe.bridge.appmovil.domain.usecases

import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(
    private val repository: MessageRepository,
) {
    operator fun invoke(): Flow<List<SmsMessage>> = repository.getMessages()
}
