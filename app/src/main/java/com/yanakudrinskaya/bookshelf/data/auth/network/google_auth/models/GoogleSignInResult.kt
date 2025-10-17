package com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.models

sealed class GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult()
    data class Error(val message: String) : GoogleSignInResult()
}