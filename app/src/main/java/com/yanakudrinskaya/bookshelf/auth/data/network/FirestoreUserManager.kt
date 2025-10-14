package com.yanakudrinskaya.bookshelf.auth.data.network

import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.auth.data.mappers.UserFirestoreMapper
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestoreException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

class FirestoreUserManager(
    private val firestore: FirebaseFirestore,
    private val userFirestoreMapper: UserFirestoreMapper
) {

    suspend fun createUserWithBookshelf(user: User): Result<User> {
        return try {
            val bookshelfRef = firestore.collection(COLLECTION_BOOKSHELVES).document()
            val bookshelfId = bookshelfRef.id
            val userWithBookshelf = user.copy(bookshelfId = bookshelfId)

            firestore.runTransaction { transaction ->
                transaction.set(
                    firestore.collection(COLLECTION_USERS).document(userWithBookshelf.userId),
                    userFirestoreMapper.userToFirestoreMap(userWithBookshelf)
                )
                transaction.set(
                    bookshelfRef,
                    mapOf(
                        FIELD_BOOK_IDS to emptyList<String>(),
                        FIELD_OWNER_ID to userWithBookshelf.userId
                    )
                )
            }.await()

            Result.Success(userWithBookshelf)
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseFirestoreException) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        } catch (e: CancellationException) {
            Result.Error(ResponseStatus.CANCELLED, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    suspend fun getUserDocument(userId: String): Result<User> {
        return try {
            val documentSnapshot = firestore.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .await()

            val documentData = documentSnapshot.data ?: return Result.Error(
                ResponseStatus.NOT_FOUND,
                "User document not found"
            )

            val user = userFirestoreMapper.documentToUser(documentData, userId)
            Result.Success(user)
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseFirestoreException) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        } catch (e: CancellationException) {
            Result.Error(ResponseStatus.CANCELLED, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    suspend fun findUserByEmail(email: String): Result<User> {
        return try {
            val querySnapshot = firestore.collection(COLLECTION_USERS)
                .whereEqualTo(FIELD_EMAIL, email)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                val document = querySnapshot.documents[0]
                val user = userFirestoreMapper.documentToUser(document.data!!, document.id)
                Result.Success(user)
            } else {
                Result.Error(ResponseStatus.NOT_FOUND, "User with email $email not found")
            }
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseFirestoreException) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        } catch (e: CancellationException) {
            Result.Error(ResponseStatus.CANCELLED, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    suspend fun updateUserName(userId: String, newName: String): Result<User> {
        return try {
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .update(FIELD_NAME, newName)
                .await()

            // Получаем обновленного пользователя
            getUserDocument(userId).let { result ->
                when (result) {
                    is Result.Success -> Result.Success(result.data.copy(name = newName))
                    is Result.Error -> result
                }
            }
        } catch (e: SocketTimeoutException) {
            Result.Error(ResponseStatus.NO_INTERNET, e.message)
        } catch (e: FirebaseFirestoreException) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        } catch (e: CancellationException) {
            Result.Error(ResponseStatus.CANCELLED, e.message)
        } catch (e: Exception) {
            Result.Error(ResponseStatus.SERVER_ERROR, e.message)
        }
    }

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_BOOKSHELVES = "bookshelves"
        private const val FIELD_NAME = "name"
        private const val FIELD_EMAIL = "email"
        private const val FIELD_BOOK_IDS = "bookIds"
        private const val FIELD_OWNER_ID = "ownerId"
    }
}