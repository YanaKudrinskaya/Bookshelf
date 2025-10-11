package com.yanakudrinskaya.bookshelf.utils

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val status: ResponseStatus,
        val message: String? = null
    ) : Result<Nothing>()
}