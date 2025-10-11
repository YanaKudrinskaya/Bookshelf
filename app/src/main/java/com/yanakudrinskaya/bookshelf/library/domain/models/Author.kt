package com.yanakudrinskaya.bookshelf.library.domain.models

data class Author(
    val id: String = "",
    val lastName: String,
    val firstName: String,
    val middleName: String = ""
) {
    fun getFullName(): String {
        return listOf(lastName, firstName, middleName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

}