package com.yanakudrinskaya.bookshelf.auth.domain.impl

import com.yanakudrinskaya.bookshelf.auth.domain.api.AuthRepository
import com.yanakudrinskaya.bookshelf.auth.domain.api.AuthInteractor
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
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

    override fun getCurrentUser(): Flow<Result<User>> = authRepository.getCurrentUser()
}