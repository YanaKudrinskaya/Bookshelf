package com.yanakudrinskaya.bookshelf.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.domain.models.User
import com.yanakudrinskaya.bookshelf.domain.models.Result
import com.yanakudrinskaya.bookshelf.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(authResult.user?.uid?: "", name, email)

            firestore.collection("users").document(authResult.user?.uid ?: "").set(
                mapOf("name" to name, "email" to email)
            ).await()
            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
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