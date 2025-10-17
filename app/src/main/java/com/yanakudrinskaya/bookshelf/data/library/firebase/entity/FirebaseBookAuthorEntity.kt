package com.yanakudrinskaya.bookshelf.data.library.firebase.entity

data class FirebaseBookAuthorEntity(
    val bookId: String = "",
    val authorId: String = ""
) {
    constructor() : this("", "")
}