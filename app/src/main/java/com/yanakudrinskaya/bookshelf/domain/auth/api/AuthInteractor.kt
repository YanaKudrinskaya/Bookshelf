package com.yanakudrinskaya.bookshelf.domain.auth.api

import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

interface AuthInteractor {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithYandex(): Result<User>
    fun getCurrentUser(): Flow<Result<User>>
}