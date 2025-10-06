package com.yanakudrinskaya.bookshelf.auth.ui.models

sealed class RequestStatus {
    data class Error(
        val message: String
    ) : RequestStatus()

    object Success : RequestStatus()
}