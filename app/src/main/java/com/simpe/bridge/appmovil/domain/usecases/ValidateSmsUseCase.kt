package com.simpe.bridge.appmovil.domain.usecases

class ValidateSmsUseCase {
    operator fun invoke(sender: String, body: String): Boolean {
        if (sender.isBlank()) return false
        if (body.isBlank()) return false
        if (body.length > 2000) return false // Protection
        return true
    }
}
