package com.yanakudrinskaya.bookshelf.main.domain.models

data class Book (
    var filePath: String,
    var title: String,
    var author: String,
    var publisher: String?,
    var date: String?,
    var readStatus: Boolean = false
)
