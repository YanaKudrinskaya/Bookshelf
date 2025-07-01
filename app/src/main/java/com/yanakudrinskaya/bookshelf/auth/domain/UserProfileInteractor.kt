package com.yanakudrinskaya.bookshelf.auth.domain

import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result

interface UserProfileInteractor {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getCurrentUser(): Result<User>
    suspend fun getLocalUser(): Result<User>
    suspend fun changeUserName(newName: String): Result<Unit>
    fun logout()
}