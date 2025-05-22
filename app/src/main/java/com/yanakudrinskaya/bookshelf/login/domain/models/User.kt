package com.yanakudrinskaya.bookshelf.login.domain.models

data class User(
    var userId: String,
    var name: String,
    var email: String,
    var bookshelfId: String = "",
    var readBooks: List<String> = emptyList()
)

