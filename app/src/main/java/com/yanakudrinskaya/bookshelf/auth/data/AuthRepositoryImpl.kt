package com.yanakudrinskaya.bookshelf.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.domain.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            // Создаем новый документ bookshelf и получаем его ID
            val bookshelfRef = firestore.collection("bookshelves").document()
            val bookshelfId = bookshelfRef.id

            val user = User(
                userId = authResult.user?.uid ?: throw Exception("User ID is null"),
                name = name,
                email = email,
                bookshelfId = bookshelfId
            )



            firestore.runTransaction { transaction ->

                // Создаем документ пользователя
                transaction.set(
                    firestore.collection("users").document(user.userId),
                    user.toFirestoreMap()
                )

                transaction.set(
                    bookshelfRef,
                    mapOf(
                        "bookIds" to emptyList<String>(), // Пустой список книг
                        "ownerId" to user.userId // ID владельца
                    )
                )

            }.await()

            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    private fun User.toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to userId,
            "name" to name,
            "email" to email,
            "bookshelfId" to bookshelfId,
            "readBooks" to readBooks //список  id книг
        )
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user?.let { firebaseUser ->
                getUserDocument(firebaseUser.uid)
            } ?: throw Exception("User not found")

            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return auth.currentUser?.let { firebaseUser ->
            try {
                Result.Success(getUserDocument(firebaseUser.uid))
            } catch (e: Exception) {
                Result.Failure(e)
            }
        } ?: Result.Failure(Exception("No authenticated user"))
    }

    override suspend fun updateUserName(newName: String): Result<User> {
        return try {
            val currentUser =
                auth.currentUser ?: return Result.Failure(Exception("No authenticated user"))
            val userId = currentUser.uid

            firestore.collection("users")
                .document(userId)
                .update("name", newName)
                .await()

            val updatedUser = getUserDocument(userId).copy(name = newName)
            Result.Success(updatedUser)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


    override fun logout() {
        auth.signOut()
    }

    private suspend fun getUserDocument(userId: String): User {
        val document = firestore.collection("users")
            .document(userId)
            .get()
            .await()

        return User(
            userId = userId,
            name = document.getString("name") ?: "",
            email = document.getString("email") ?: "",
            bookshelfId = document.getString("bookshelfId") ?: "",
            readBooks = document.get("readBooks") as? List<String> ?: emptyList()
        )
    }

}