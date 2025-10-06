package com.yanakudrinskaya.bookshelf.library.data.firebase.entity

class FirebaseWorkAuthorEntity (
    val workId: String = "",
    val authorId: String = ""
) {
    constructor() : this("", "")
}