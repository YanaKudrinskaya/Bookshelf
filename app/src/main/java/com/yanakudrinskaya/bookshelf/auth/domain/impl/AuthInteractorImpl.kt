package com.yanakudrinskaya.bookshelf.auth.domain.impl

import com.yanakudrinskaya.bookshelf.auth.domain.AuthInteractor
import com.yanakudrinskaya.bookshelf.auth.domain.AuthRepository
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileRepository
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

class AuthInteractorImpl(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository
): AuthInteractor {
    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return authRepository.register(name, email, password)
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }

    override fun getCurrentUser(): Flow<Result<User>> = authRepository.getCurrentUser()


    override fun getLocalUserProfileStream(): Flow<Result<User>> {
        return userProfileRepository.getLocalUserProfileStream()
    }

    override suspend fun updateUserName(newName: String): Result<User> {
        return authRepository.updateUserName(newName)
    }

    override fun logout() {
        authRepository.logout()
    }

}