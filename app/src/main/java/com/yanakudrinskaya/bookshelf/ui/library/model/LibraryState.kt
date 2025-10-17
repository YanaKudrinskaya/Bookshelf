package com.yanakudrinskaya.bookshelf.ui.library.model

import com.yanakudrinskaya.bookshelf.domain.library.models.Book

sealed interface LibraryState {
    object Loading : LibraryState

    data class Content(
        val books: List<Book>
    ) : LibraryState

    object Error : LibraryState

    object Empty : LibraryState
}