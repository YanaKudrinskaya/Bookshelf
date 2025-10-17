package com.yanakudrinskaya.bookshelf.data.auth.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import kotlinx.coroutines.tasks.await
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

class FirebaseAuthProvider(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreUserManager: FirestoreUserManager
) : AuthProvider {

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")

            val user = User(
                userId = userId,
                name = name,
                email = email,
                bookshelfId = ""
            )

            firestoreUserManager.createUserWithBookshelf(user)
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseAuthException) {
            Result.Error(ResponseStatus.AUTH_ERROR, e.message)
        } catch (e: CancellationException) {
            Result.Error(ResponseStatus.CANCELLED, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User not found")

            firestoreUserManager.getUserDocument(userId)
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseAuthException) {
            Result.Error(ResponseStatus.AUTH_ERROR, e.message)
        } catch (e: CancellationException) {
            Result.Error(ResponseStatus.CANCELLED, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val userResult = firestoreUserManager.getUserDocument(firebaseUser.uid)
                userResult
            } else {
                Result.Error(ResponseStatus.UNAUTHORIZED, "User not authenticated")
            }
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    override suspend fun updateUserName(newName: String): Result<User> {
        return try {
            val currentUser = firebaseAuth.currentUser

            if (currentUser == null) {
                return Result.Error(
                    ResponseStatus.UNAUTHORIZED,
                    "User not authenticated"
                )
            }

            val result = firestoreUserManager.updateUserName(currentUser.uid, newName)
            result
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    override fun logout() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            println("Logout error: ${e.message}")
        }
    }
}