package com.yanakudrinskaya.bookshelf.library.domain.models

data class Book (
    var id: String = "",
    var filePath: String = "",
    var title: String,
    var author: String = "",
    var publisher: String?,
    var date: String?,
    var worksId: List<String> = emptyList(),
    var readStatus: Boolean = false
)
