package com.yanakudrinskaya.bookshelf.add_book.ui.models

import com.yanakudrinskaya.bookshelf.library.domain.models.Work

sealed interface AddWorkScreenState {
    data class WorksSearchResults(val works: List<Work>) : AddWorkScreenState
    data object AuthorsIsEmpty: AddWorkScreenState
}