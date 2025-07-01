package com.yanakudrinskaya.bookshelf.auth.ui.models

sealed class RequestStatus {
    data class Error(
        val message: String,
        val status: EditStatus = EditStatus.NONE,
    ) : RequestStatus()

    object Success : RequestStatus()
}