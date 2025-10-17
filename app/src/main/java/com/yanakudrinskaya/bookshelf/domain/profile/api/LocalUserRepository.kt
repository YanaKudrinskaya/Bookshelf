package com.yanakudrinskaya.bookshelf.domain.profile.api

import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

interface LocalUserRepository {
    fun saveLocalUserProfile(user: User)
    fun getLocalUserProfile(): Result<User>
    fun getLocalUserProfileStream(): Flow<Result<User>>
    fun deleteProfile()
}