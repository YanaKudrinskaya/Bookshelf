package com.yanakudrinskaya.bookshelf.login.domain

import com.yanakudrinskaya.bookshelf.Result
import com.yanakudrinskaya.bookshelf.login.domain.models.User

interface UserProfileRepository {
    suspend fun saveLocalUserProfile(user: User)
    suspend fun getLocalUserProfile(): Result<User>
    fun deleteProfile()
}