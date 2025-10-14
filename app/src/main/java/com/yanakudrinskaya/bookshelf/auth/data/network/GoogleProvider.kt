package com.yanakudrinskaya.bookshelf.auth.data.network

import com.yanakudrinskaya.bookshelf.auth.data.network.google_auth.models.GoogleAuthResult
import com.yanakudrinskaya.bookshelf.utils.Result

interface GoogleProvider {
    suspend fun signInWithGoogle(idToken: String): Result<GoogleAuthResult>
}