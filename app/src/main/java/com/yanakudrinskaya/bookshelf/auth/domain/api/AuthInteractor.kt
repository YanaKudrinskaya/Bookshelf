package com.yanakudrinskaya.bookshelf.auth.domain.api

import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

interface AuthInteractor {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    fun getCurrentUser(): Flow<Result<User>>
    fun getLocalUserProfileStream(): Flow<Result<User>>
    suspend fun updateUserName(newName: String): Result<User>
    fun logout()
}