package com.yanakudrinskaya.bookshelf.add_book.ui.models

sealed interface BookState {

    object Loading : BookState

    data class Error(
        val e: String
    ) : BookState

    object Success : BookState
}