package com.yanakudrinskaya.bookshelf.domain.auth.impl

import com.yanakudrinskaya.bookshelf.domain.auth.api.AuthRepository
import com.yanakudrinskaya.bookshelf.domain.auth.api.AuthInteractor
import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

class AuthInteractorImpl(
    private val authRepository: AuthRepository
): AuthInteractor {
    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return authRepository.register(name, email, password)
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return authRepository.signInWithGoogle(idToken)
    }

    override suspend fun signInWithYandex(): Result<User> {
        return authRepository.signInWithYandex()
    }

    override fun getCurrentUser(): Flow<Result<User>> = authRepository.getCurrentUser()
}