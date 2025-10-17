package com.yanakudrinskaya.bookshelf.data.profile

import com.yanakudrinskaya.bookshelf.data.auth.network.AuthProvider
import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.domain.profile.api.LocalUserRepository
import com.yanakudrinskaya.bookshelf.domain.profile.api.UserProfileRepository
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import kotlinx.coroutines.flow.Flow

class UserProfileRepositoryImpl(
    private val authProvider: AuthProvider,
    private val localDataSource: LocalUserRepository
) : UserProfileRepository {

    override suspend fun updateUserName(newName: String) {
        authProvider.updateUserName(newName).also { result ->
            if (result is Result.Success) {
                localDataSource.saveLocalUserProfile(result.data)
            }
        }
    }

    override suspend fun updateUserNameLocally(userId: String, newName: String): Result<User> {
        return try {
            val currentUserResult = localDataSource.getLocalUserProfile()
            if (currentUserResult is Result.Success) {
                val updatedUser = currentUserResult.data.copy(name = newName)
                localDataSource.saveLocalUserProfile(updatedUser)
                Result.Success(updatedUser)
            } else {
                Result.Error(ResponseStatus.NOT_FOUND, "User not found in local storage")
            }
        } catch (e: Exception) {
            Result.Error(ResponseStatus.UNKNOWN_ERROR, "Local update failed: ${e.message}")
        }
    }

    override fun getLocalUserProfileStream(): Flow<Result<User>> {
        return localDataSource.getLocalUserProfileStream()
    }
}