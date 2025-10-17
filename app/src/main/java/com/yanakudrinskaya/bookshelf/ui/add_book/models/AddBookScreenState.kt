package com.yanakudrinskaya.bookshelf.ui.add_book.models

import com.yanakudrinskaya.bookshelf.domain.library.models.Author
import com.yanakudrinskaya.bookshelf.domain.library.models.Work

sealed interface AddBookScreenState {
    object Loading : AddBookScreenState

    data class Content(val content: MutableList<Work>) : AddBookScreenState
    data class NavigateFragment(val fragment: NavigateFragmentEvent) : AddBookScreenState

    data class Error(val e: String) : AddBookScreenState

    data class AuthorSearchResults(val authors: List<Author>) : AddBookScreenState
    data class AuthorSelected(val author: Author) : AddBookScreenState


    object Success : AddBookScreenState
}