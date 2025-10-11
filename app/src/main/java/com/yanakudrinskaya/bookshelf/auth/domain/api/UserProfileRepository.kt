package com.yanakudrinskaya.bookshelf.auth.domain

import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun saveLocalUserProfile(user: User)
    fun getLocalUserProfile(): Result<User>
    fun getLocalUserProfileStream(): Flow<Result<User>>
    fun deleteProfile()
}