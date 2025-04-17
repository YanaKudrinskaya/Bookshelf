package com.yanakudrinskaya.bookshelf.domain.impl

import android.util.Log
import com.yanakudrinskaya.bookshelf.domain.models.UserCurrent
import com.yanakudrinskaya.bookshelf.domain.interactors.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.domain.models.User
import com.yanakudrinskaya.bookshelf.domain.models.Result
import com.yanakudrinskaya.bookshelf.domain.repository.AuthRepository
import com.yanakudrinskaya.bookshelf.domain.repository.NetworkMonitorRepository
import com.yanakudrinskaya.bookshelf.domain.repository.UserProfileRepository

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
                    saveCurrentuser(result.data)
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
                    saveCurrentuser(result.data)
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


    override fun logout() {
        authRepository.logout()
        userProfileRepository.deleteProfile()
    }

    private fun saveCurrentuser(user : User) {
        UserCurrent.id = user.userId
        UserCurrent.name = user.name
        UserCurrent.email = user.email
    }

}