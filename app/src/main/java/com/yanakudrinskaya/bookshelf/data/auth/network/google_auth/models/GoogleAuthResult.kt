package com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.models

data class GoogleAuthResult(
    val userId: String,
    val email: String?,
    val displayName: String?,
    val isNewUser: Boolean = false
)