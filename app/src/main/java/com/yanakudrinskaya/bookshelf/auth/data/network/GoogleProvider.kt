package com.yanakudrinskaya.bookshelf.auth.data.network

import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.utils.Result

interface GoogleProvider {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun linkWithGoogle(idToken: String): Result<User>
    fun signOut()
}