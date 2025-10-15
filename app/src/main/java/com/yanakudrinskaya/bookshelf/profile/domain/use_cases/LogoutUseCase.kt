package com.yanakudrinskaya.bookshelf.profile.domain.use_cases

import com.yanakudrinskaya.bookshelf.auth.domain.api.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
) {
    fun logout() {
        authRepository.logout()
    }
}