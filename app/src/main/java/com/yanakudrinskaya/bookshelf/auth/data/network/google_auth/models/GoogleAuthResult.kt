package com.yanakudrinskaya.bookshelf.auth.data.network.google_auth.models

data class GoogleAuthResult(
    val userId: String,
    val email: String?,
    val displayName: String?,
    val isNewUser: Boolean = false
)