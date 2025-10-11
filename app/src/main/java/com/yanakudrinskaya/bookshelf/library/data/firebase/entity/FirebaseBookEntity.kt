package com.yanakudrinskaya.bookshelf.library.data.firebase.entity

data class FirebaseBookEntity(
    val title: String = "",
    val publisher: String = "",
    val date: String = ""
) {
    constructor() : this( "", "", "")
}