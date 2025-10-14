package com.yanakudrinskaya.bookshelf.auth.data

import com.yanakudrinskaya.bookshelf.auth.data.network.AuthProvider
import com.yanakudrinskaya.bookshelf.auth.data.network.FirestoreUserManager
import com.yanakudrinskaya.bookshelf.auth.data.network.GoogleProvider
import com.yanakudrinskaya.bookshelf.auth.data.network.google_auth.models.GoogleAuthResult
import com.yanakudrinskaya.bookshelf.auth.data.utils.NetworkMonitor
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.domain.AuthRepository
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileRepository
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authProvider: AuthProvider,
    private val googleAuthProvider: GoogleProvider,
    private val localDataSource: UserProfileRepository,
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

    private suspend fun handleGoogleSignIn(googleAuthResult: GoogleAuthResult): Result<User> {
        return try {
            val email = googleAuthResult.email
            if (email.isNullOrEmpty()) {
                return Result.Error(
                    com.yanakudrinskaya.bookshelf.utils.ResponseStatus.AUTH_ERROR,
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
                    if (userResult.status == com.yanakudrinskaya.bookshelf.utils.ResponseStatus.NOT_FOUND) {
                        registerNewGoogleUser(googleAuthResult)
                    } else {
                        userResult
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(
                com.yanakudrinskaya.bookshelf.utils.ResponseStatus.SERVER_ERROR,
                "Google sign in failed: ${e.message}"
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
}
