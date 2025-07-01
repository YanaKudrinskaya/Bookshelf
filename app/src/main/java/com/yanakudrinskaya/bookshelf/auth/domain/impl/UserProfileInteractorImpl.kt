package com.yanakudrinskaya.bookshelf.auth.domain.impl

import android.util.Log
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.domain.AuthRepository
import com.yanakudrinskaya.bookshelf.auth.domain.NetworkMonitorRepository
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileRepository
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileInteractor

class UserProfileInteractorImpl(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
    private val networkMonitor: NetworkMonitorRepository
) : UserProfileInteractor {

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return authRepository.register(name, email, password).let { result ->
            when (result) {
                is Result.Success -> {
                    userProfileRepository.saveLocalUserProfile(result.data)
                    result
                }

                is Result.Failure -> {
                    result
                }
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return authRepository.login(email, password).let { result ->
            when (result) {
                is Result.Success -> {
                    userProfileRepository.saveLocalUserProfile(result.data)
                    result
                }

                is Result.Failure -> {
                    result
                }
            }
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return if (networkMonitor.isNetworkAvailable()) {
            authRepository.getCurrentUser().let { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("Myregister", "Получила User из Firebase")
                        userProfileRepository.saveLocalUserProfile(result.data)
                        result
                    }
                    is Result.Failure -> {
                        Log.d("Myregister", "Пробую загрузить из преференса")
                        getLocalUser()
                    }
                }
            }
        } else {
            Log.d("Myregister", "Нет сети. Пробую загрузить из преференса")
            getLocalUser()}
    }

    override suspend fun getLocalUser(): Result<User> {
        return userProfileRepository.getLocalUserProfile()
    }

    override suspend fun changeUserName(newName: String): Result<Unit> {
        return if (networkMonitor.isNetworkAvailable()) {
            authRepository.updateUserName(newName).let { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("Myregister", "Изменила имя на newName")
                        userProfileRepository.saveLocalUserProfile(result.data)
                        Result.Success(Unit)
                    }
                    is Result.Failure -> {
                        Log.d("Myregister", "Ошибка сохранения нового имени пользователя")
                        result
                    }
                }
            }
        } else {
            Result.Failure(Exception("Нет сети. Попробуйте позже."))
        }
    }


    override fun logout() {
        authRepository.logout()
        userProfileRepository.deleteProfile()
    }



}