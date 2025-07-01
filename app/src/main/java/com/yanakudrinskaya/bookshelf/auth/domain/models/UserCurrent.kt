package com.yanakudrinskaya.bookshelf.auth.domain.models


object UserCurrent {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var bookshelfId: String = ""
    var readBooks: List<String> = emptyList()
}