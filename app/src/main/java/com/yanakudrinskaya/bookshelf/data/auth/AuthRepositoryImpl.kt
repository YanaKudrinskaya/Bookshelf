package com.yanakudrinskaya.bookshelf.data.auth

import com.yanakudrinskaya.bookshelf.data.auth.network.AuthProvider
import com.yanakudrinskaya.bookshelf.data.auth.network.FirestoreUserManager
import com.yanakudrinskaya.bookshelf.data.auth.network.GoogleProvider
import com.yanakudrinskaya.bookshelf.data.auth.network.NetworkClient
import com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.models.GoogleAuthResult
import com.yanakudrinskaya.bookshelf.data.auth.network.yandex_auth.YandexAuthService
import com.yanakudrinskaya.bookshelf.data.auth.network.yandex_auth.dto.RequestDto
import com.yanakudrinskaya.bookshelf.data.auth.network.yandex_auth.dto.response.YandexUserInfoResponse
import com.yanakudrinskaya.bookshelf.data.auth.mappers.UserMapper
import com.yanakudrinskaya.bookshelf.utils.NetworkMonitor
import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.domain.auth.api.AuthRepository
import com.yanakudrinskaya.bookshelf.domain.profile.api.LocalUserRepository
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authProvider: AuthProvider,
    private val googleAuthProvider: GoogleProvider,
    private val yandexAuthService: YandexAuthService,
    private val yandexNetworkClient: NetworkClient,
    private val localDataSource: LocalUserRepository,
    private val networkMonitor: NetworkMonitor,
    private val firestoreUserManager: FirestoreUserManager
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


    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return when (val googleResult = googleAuthProvider.signInWithGoogle(idToken)) {
            is Result.Success -> {
                handleGoogleSignIn(googleResult.data)
            }

            is Result.Error -> googleResult
        }
    }

    override suspend fun signInWithYandex(): Result<User> {
        return try {
            // 1. Получаем токен через YandexAuthService
            when (val tokenResult = yandexAuthService.login()) {
                is Result.Success -> {
                    // 2. Получаем информацию о пользователе через NetworkClient
                    val userInfoResult = getUserInfoFromYandex(tokenResult.data.value)
                    when (userInfoResult) {
                        is Result.Success -> {
                            handleYandexSignIn(userInfoResult.data)
                        }
                        is Result.Error -> userInfoResult
                    }
                }
                is Result.Error -> tokenResult
            }
        } catch (e: Exception) {
            Result.Error(
                ResponseStatus.UNKNOWN_ERROR,
                "Yandex authentication failed: ${e.message}"
            )
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

    override fun logout() {
        authProvider.logout()
        localDataSource.deleteProfile()
    }

    private suspend fun handleGoogleSignIn(googleAuthResult: GoogleAuthResult): Result<User> {
        return try {
            val email = googleAuthResult.email
            if (email.isNullOrEmpty()) {
                return Result.Error(
                    ResponseStatus.AUTH_ERROR,
                    "Email not provided by Google"
                )
            }
            // Ищем пользователя по email в Firestore
            val userResult = firestoreUserManager.findUserByEmail(email)
            when (userResult) {
                is Result.Success -> {
                    // Пользователь найден - выполняем вход
                    localDataSource.saveLocalUserProfile(userResult.data)
                    userResult
                }
                is Result.Error -> {
                    // Пользователь не найден - регистрируем нового
                    if (userResult.status == ResponseStatus.NOT_FOUND) {
                        registerNewGoogleUser(googleAuthResult)
                    } else {
                        userResult
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(
                ResponseStatus.SERVER_ERROR,
                "Google sign in failed: ${e.message}"
            )
        }
    }

    private suspend fun handleYandexSignIn(yandexUser: User): Result<User> {
        return try {
            val email = yandexUser.email
            if (email.isNullOrEmpty()) {
                return Result.Error(
                    ResponseStatus.AUTH_ERROR,
                    "Email not provided by Yandex"
                )
            }
            // Ищем пользователя по email в Firestore
            val userResult = firestoreUserManager.findUserByEmail(email)
            when (userResult) {
                is Result.Success -> {
                    // Пользователь найден - выполняем вход
                    localDataSource.saveLocalUserProfile(userResult.data)
                    userResult
                }
                is Result.Error -> {
                    // Пользователь не найден - регистрируем нового
                    if (userResult.status == ResponseStatus.NOT_FOUND) {
                        registerNewYandexUser(yandexUser)
                    } else {
                        userResult
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(
                ResponseStatus.SERVER_ERROR,
                "Yandex sign in failed: ${e.message}"
            )
        }
    }

    private suspend fun registerNewGoogleUser(googleAuthResult: GoogleAuthResult): Result<User> {
        val user = User(
            userId = googleAuthResult.userId,
            name = googleAuthResult.displayName ?: "Google User",
            email = googleAuthResult.email ?: "",
            bookshelfId = ""
        )

        // Создаем пользователя в Firestore
        return when (val createResult = firestoreUserManager.createUserWithBookshelf(user)) {
            is Result.Success -> {
                // Сохраняем локально ТОЛЬКО при успешном создании на сервере
                localDataSource.saveLocalUserProfile(createResult.data)
                createResult
            }
            is Result.Error -> {
                // НЕ сохраняем локально при ошибке - пользователь должен повторить попытку
                createResult
            }
        }
    }

    private suspend fun getUserInfoFromYandex(token: String): Result<User> {
        val request = RequestDto.UserInfoWithTokenRequest(token)
        val response = yandexNetworkClient.doRequest(request)

        return when (response.status) {
            ResponseStatus.SUCCESS -> {
                val userInfoResponse = response as? YandexUserInfoResponse
                if (userInfoResponse != null) {
                    val user = UserMapper.mapToDomain(userInfoResponse)
                    Result.Success(user)
                } else {
                    Result.Error(ResponseStatus.UNKNOWN_ERROR, "Invalid response format from Yandex")
                }
            }
            else -> {
                Result.Error(response.status, response.errorMessage ?: "Failed to get user info from Yandex")
            }
        }
    }

    private suspend fun registerNewYandexUser(yandexUser: User): Result<User> {
        // Создаем пользователя в Firestore
        return when (val createResult = firestoreUserManager.createUserWithBookshelf(yandexUser)) {
            is Result.Success -> {
                // Сохраняем локально ТОЛЬКО при успешном создании на сервере
                localDataSource.saveLocalUserProfile(createResult.data)
                createResult
            }
            is Result.Error -> {
                // НЕ сохраняем локально при ошибке - пользователь должен повторить попытку
                createResult
            }
        }
    }
}
