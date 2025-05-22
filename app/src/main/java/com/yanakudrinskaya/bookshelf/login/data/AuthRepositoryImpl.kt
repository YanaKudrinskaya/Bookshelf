package com.yanakudrinskaya.bookshelf.login.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.login.domain.models.User
import com.yanakudrinskaya.bookshelf.Result
import com.yanakudrinskaya.bookshelf.login.domain.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val bookshelfId = firestore.collection("bookshelf").document().id

            val user = User(
                userId =  authResult.user?.uid ?: throw Exception("User ID is null"),
                name = name,
                email = email,
                bookshelfId = bookshelfId
            )



            firestore.runTransaction { transaction ->
                // Создаем коллекцию bookshelf
                transaction.set(
                    firestore.collection("bookshelves").document(bookshelfId),
                    mapOf("ownerId" to user.userId)
                )

                // Создаем документ пользователя
                transaction.set(
                    firestore.collection("users").document(user.userId),
                    user.toFirestoreMap()
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
            "readBooks" to readBooks
        )
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user?.let { firebaseUser ->
                val document = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                User(
                    userId = firebaseUser.uid,
                    name = document.getString("name") ?: "",
                    email = firebaseUser.email ?: ""
                )
            } ?: throw Exception("User not found")

            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return auth.currentUser?.let { firebaseUser ->
            val document = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                Result.Success(User(firebaseUser.uid,document.getString("name") ?: "", document.getString("email")?: ""))
        } ?: Result.Failure(Exception("No authenticated user"))
    }

    override fun logout() {
        auth.signOut()
    }
}