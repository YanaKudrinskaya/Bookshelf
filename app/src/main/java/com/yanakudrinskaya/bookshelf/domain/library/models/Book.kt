package com.yanakudrinskaya.bookshelf.domain.library.models

data class Book (
    var id: String = "",
    var filePath: String = "",
    var title: String,
    var authors: List<Author> = emptyList(),
    var publisher: String = "",
    var date: String = "",
    var works: List<Work> = emptyList(),
    var readStatus: Boolean = false
)
