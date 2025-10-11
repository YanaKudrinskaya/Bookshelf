package com.yanakudrinskaya.bookshelf.library.ui.model

import com.yanakudrinskaya.bookshelf.library.domain.models.Book

sealed interface LibraryState {
    object Loading : LibraryState

    data class Content(
        val books: List<Book>
    ) : LibraryState

    object Error : LibraryState

    object Empty : LibraryState
}