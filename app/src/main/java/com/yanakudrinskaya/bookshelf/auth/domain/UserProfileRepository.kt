package com.yanakudrinskaya.bookshelf.auth.domain

import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.domain.models.User

interface UserProfileRepository {
    suspend fun saveLocalUserProfile(user: User)
    suspend fun getLocalUserProfile(): Result<User>
    fun deleteProfile()
}