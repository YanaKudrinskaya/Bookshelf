package com.yanakudrinskaya.bookshelf.data.library.firebase.entity


data class FirebaseAuthorEntity(
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = ""
)
 {
    constructor() : this("", "", "")
}
