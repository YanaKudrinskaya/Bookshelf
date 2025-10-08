package com.yanakudrinskaya.bookshelf.auth.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.yanakudrinskaya.bookshelf.auth.data.network.AuthProvider
import com.yanakudrinskaya.bookshelf.auth.data.mappers.UserFirestoreMapper
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import kotlinx.coroutines.tasks.await
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

class FirebaseAuthProvider(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userFirestoreMapper: UserFirestoreMapper
) : AuthProvider {

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")

            val bookshelfRef = firestore.collection(COLLECTION_BOOKSHELVES).document()
            val bookshelfId = bookshelfRef.id

            val user = User(
                userId = userId,
                name = name,
                email = email,
                bookshelfId = bookshelfId
            )

            firestore.runTransaction { transaction ->
                transaction.set(
                    firestore.collection(COLLECTION_USERS).document(user.userId),
                    userFirestoreMapper.userToFirestoreMap(user)
                )
                transaction.set(
                    bookshelfRef,
                    mapOf(
                        FIELD_BOOK_IDS to emptyList<String>(),
                        FIELD_OWNER_ID to user.userId
                    )
                )
            }.await()

            Result.Success(user)
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseAuthException) {
            Result.Error(ResponseStatus.AUTH_ERROR, e.message)
        } catch (e: FirebaseFirestoreException) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        } catch (e: CancellationException) {
            Result.Error(ResponseStatus.CANCELLED, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user?.let { firebaseUser ->
                getUserDocument(firebaseUser.uid)
            } ?: throw Exception("User not found")

            Result.Success(user)
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseAuthException) {
            Result.Error(ResponseStatus.AUTH_ERROR, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: return Result.Error(
                ResponseStatus.UNAUTHORIZED,
                "User not authenticated"
            )
            Result.Success(getUserDocument(firebaseUser.uid))
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    override suspend fun updateUserName(newName: String): Result<User> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: return Result.Error(
                ResponseStatus.UNAUTHORIZED,
                "User not authenticated"
            )
            val userId = currentUser.uid

            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .update(FIELD_NAME, newName)
                .await()

            val updatedUser = getUserDocument(userId).copy(name = newName)
            Result.Success(updatedUser)
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

    private suspend fun getUserDocument(userId: String): User {
        val documentSnapshot = firestore.collection(COLLECTION_USERS)
            .document(userId)
            .get()
            .await()

        val documentData = documentSnapshot.data ?: throw Exception("User document not found")

        return userFirestoreMapper.documentToUser(documentData, userId)
    }

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_BOOKSHELVES = "bookshelves"
        private const val FIELD_NAME = "name"
        private const val FIELD_BOOK_IDS = "bookIds"
        private const val FIELD_OWNER_ID = "ownerId"
    }
}