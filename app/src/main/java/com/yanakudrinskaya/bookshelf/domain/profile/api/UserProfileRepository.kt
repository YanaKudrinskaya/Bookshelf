package com.yanakudrinskaya.bookshelf.domain.profile.api

import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun updateUserName(newName: String)
    suspend fun updateUserNameLocally(userId: String, newName: String): Result<User>
    fun getLocalUserProfileStream(): Flow<Result<User>>
}