package com.yanakudrinskaya.bookshelf.data.auth.network.google_auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yanakudrinskaya.bookshelf.data.auth.network.GoogleProvider
import com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.models.GoogleAuthResult
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.tasks.await

class FirebaseGoogleProvider(
    private val firebaseAuth: FirebaseAuth
) : GoogleProvider {
    override suspend fun signInWithGoogle(idToken: String): Result<GoogleAuthResult> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            val user = authResult.user ?: throw Exception("Authentication failed")

            Result.Success(
                GoogleAuthResult(
                    userId = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                )
            )
        } catch (e: Exception) {
            Result.Error(ResponseStatus.AUTH_ERROR, "Google authentication failed")
        }
    }
}