package com.simpe.bridge.appmovil.domain.auth

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(phone: String, password: String): Result<Unit> =
        authRepository.login(phone, password)
}
