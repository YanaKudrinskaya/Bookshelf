package com.yanakudrinskaya.bookshelf.auth.data

import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result

interface AuthProvider {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getCurrentUser(): Result<User>
    suspend fun updateUserName(newName: String): Result<User>
    fun logout()
}