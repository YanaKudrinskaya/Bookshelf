package com.yanakudrinskaya.bookshelf.library.data.firebase.entity

data class FirebaseBookWorkEntity(
    val bookId: String = "",
    val workId: String = ""
) {
    constructor() : this("", "")
}