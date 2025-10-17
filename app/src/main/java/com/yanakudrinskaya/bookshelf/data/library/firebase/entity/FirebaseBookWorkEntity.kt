package com.yanakudrinskaya.bookshelf.data.library.firebase.entity

data class FirebaseBookWorkEntity(
    val bookId: String = "",
    val workId: String = ""
) {
    constructor() : this("", "")
}