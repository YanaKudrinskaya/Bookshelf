package com.yanakudrinskaya.bookshelf.domain.repository

import com.yanakudrinskaya.bookshelf.domain.models.Result
import com.yanakudrinskaya.bookshelf.domain.models.User

interface UserProfileRepository {
    suspend fun saveLocalUserProfile(user: User)
    suspend fun getLocalUserProfile(): Result<User>
    fun deleteProfile()
}