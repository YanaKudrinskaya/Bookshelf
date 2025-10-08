package com.yanakudrinskaya.bookshelf.auth.domain

import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun linkWithGoogle(idToken: String): Result<User>
    fun getCurrentUser(): Flow<Result<User>>
    suspend fun updateUserName(newName: String) : Result<User>
    fun logout()
}