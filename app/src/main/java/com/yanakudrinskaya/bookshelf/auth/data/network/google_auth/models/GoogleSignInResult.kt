package com.yanakudrinskaya.bookshelf.auth.data.network.google_auth.models

sealed class GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult()
    data class Error(val message: String) : GoogleSignInResult()
}