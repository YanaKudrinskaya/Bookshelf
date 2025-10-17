package com.yanakudrinskaya.bookshelf.domain.profile.use_cases

import com.yanakudrinskaya.bookshelf.domain.auth.api.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
) {
    fun logout() {
        authRepository.logout()
    }
}