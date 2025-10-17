package com.yanakudrinskaya.bookshelf.ui.add_book.models

sealed interface BookState {

    object Loading : BookState

    data class Error(
        val e: String
    ) : BookState

    object Success : BookState
}