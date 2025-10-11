package com.yanakudrinskaya.bookshelf.auth.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.yanakudrinskaya.bookshelf.auth.data.mappers.UserFirestoreMapper
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import kotlinx.coroutines.tasks.await
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

class FirebaseGoogleAuthProvider (
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userFirestoreMapper: UserFirestoreMapper
) : GoogleProvider {

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            val firebaseUser = authResult.user ?: throw Exception("Google authentication failed")

            // Проверяем, есть ли пользователь в Firestore
            val user = try {
                getUserDocument(firebaseUser.uid)
            } catch (e: Exception) {
                // Если пользователя нет, создаем нового
                createNewGoogleUser(firebaseUser)
            }

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

    override suspend fun linkWithGoogle(idToken: String): Result<User> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: return Result.Error(
                ResponseStatus.UNAUTHORIZED,
                "User not authenticated"
            )

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = currentUser.linkWithCredential(credential).await()

            val user = getUserDocument(authResult.user?.uid ?: currentUser.uid)
            Result.Success(user)
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseAuthException) {
            Result.Error(ResponseStatus.AUTH_ERROR, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }
    override fun signOut() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            println("Google sign out error: ${e.message}")
        }
    }
    private suspend fun createNewGoogleUser(firebaseUser: com.google.firebase.auth.FirebaseUser): User {
        val bookshelfRef = firestore.collection(COLLECTION_BOOKSHELVES).document()
        val bookshelfId = bookshelfRef.id

        val user = User(
            userId = firebaseUser.uid,
            name = firebaseUser.displayName ?: "Google User",
            email = firebaseUser.email ?: "",
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

        return user
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
        private const val FIELD_BOOK_IDS = "bookIds"
        private const val FIELD_OWNER_ID = "ownerId"
    }
}