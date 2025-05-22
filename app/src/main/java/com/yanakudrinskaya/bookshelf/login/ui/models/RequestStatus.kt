package com.yanakudrinskaya.bookshelf.login.ui.models

sealed class RequestStatus {
    data class Error(
        val message: String,
        val status: EditStatus = EditStatus.NONE,
    ) : RequestStatus()

    object Success : RequestStatus()
}