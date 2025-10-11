package com.yanakudrinskaya.bookshelf.library.data.firebase.entity

data class FirebaseBookAuthorEntity(
    val bookId: String = "",
    val authorId: String = ""
) {
    constructor() : this("", "")
}