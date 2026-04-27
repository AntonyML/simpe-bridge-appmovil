package com.simpe.bridge.appmovil.domain.usecases

class SaveMessageUseCase(
    private val repository: MessageRepository,
) {
    suspend operator fun invoke(message: SmsMessage) {
        repository.saveMessage(message)
    }
}
