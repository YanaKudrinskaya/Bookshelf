package com.yanakudrinskaya.bookshelf.login.domain

import com.yanakudrinskaya.bookshelf.login.domain.models.User
import com.yanakudrinskaya.bookshelf.Result

interface UserProfileInteractor {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getCurrentUser(): Result<User>
    suspend fun getLocalUser(): Result<User>
    fun logout()
}