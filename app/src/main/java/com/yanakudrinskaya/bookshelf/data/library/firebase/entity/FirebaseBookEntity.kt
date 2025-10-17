package com.yanakudrinskaya.bookshelf.data.library.firebase.entity

data class FirebaseBookEntity(
    val title: String = "",
    val publisher: String = "",
    val date: String = ""
) {
    constructor() : this( "", "", "")
}