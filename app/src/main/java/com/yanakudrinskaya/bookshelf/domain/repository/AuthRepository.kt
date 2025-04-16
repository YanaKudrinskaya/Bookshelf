package com.yanakudrinskaya.bookshelf.domain.repository

import com.yanakudrinskaya.bookshelf.domain.models.User
import com.yanakudrinskaya.bookshelf.domain.models.Result

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getCurrentUser(): Result<User>
    fun logout()
}