package com.yanakudrinskaya.bookshelf.ui.add_book.models

import com.yanakudrinskaya.bookshelf.domain.library.models.Work

sealed interface AddWorkScreenState {
    data class WorksSearchResults(val works: List<Work>) : AddWorkScreenState
    data object AuthorsIsEmpty: AddWorkScreenState
}