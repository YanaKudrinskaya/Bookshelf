package com.yanakudrinskaya.bookshelf.library.data.firebase.entity


data class FirebaseAuthorEntity(
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = ""
)
 {
    constructor() : this("", "", "")
}
