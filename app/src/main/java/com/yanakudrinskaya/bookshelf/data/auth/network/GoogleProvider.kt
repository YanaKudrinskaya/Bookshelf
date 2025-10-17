package com.yanakudrinskaya.bookshelf.data.auth.network

import com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.models.GoogleAuthResult
import com.yanakudrinskaya.bookshelf.utils.Result

interface GoogleProvider {
    suspend fun signInWithGoogle(idToken: String): Result<GoogleAuthResult>
}