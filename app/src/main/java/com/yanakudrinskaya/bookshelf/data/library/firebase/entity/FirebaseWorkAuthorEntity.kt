package com.yanakudrinskaya.bookshelf.data.library.firebase.entity

class FirebaseWorkAuthorEntity (
    val workId: String = "",
    val authorId: String = ""
) {
    constructor() : this("", "")
}