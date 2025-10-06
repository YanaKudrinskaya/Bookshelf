package com.yanakudrinskaya.bookshelf.auth.data

import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.domain.AuthRepository
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authProvider: AuthProvider,
    private val localDataSource: UserProfileRepository,
    private val networkMonitor: NetworkMonitor
) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return authProvider.register(name, email, password).also { result ->
            if (result is Result.Success) {
                localDataSource.saveLocalUserProfile(result.data)
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return authProvider.login(email, password).also { result ->
            if (result is Result.Success) {
                localDataSource.saveLocalUserProfile(result.data)
            }
        }
    }

    override fun getCurrentUser(): Flow<Result<User>> = flow {
        if (networkMonitor.isNetworkAvailable()) {
            val networkResult = authProvider.getCurrentUser()
            if (networkResult is Result.Success) {
                localDataSource.saveLocalUserProfile(networkResult.data)
                emit(networkResult)
                return@flow
            }
        }
        emit(localDataSource.getLocalUserProfile())
    }

    override suspend fun updateUserName(newName: String): Result<User> {
        return authProvider.updateUserName(newName).also { result ->
            if (result is Result.Success) {
                localDataSource.saveLocalUserProfile(result.data)
            }
        }
    }

    override fun logout() {
        authProvider.logout()
        localDataSource.deleteProfile()
    }
}