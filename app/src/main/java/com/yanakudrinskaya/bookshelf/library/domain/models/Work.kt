package com.yanakudrinskaya.bookshelf.library.domain.models

data class Work (
    val id: String = "",
    val authors: List<Author> = emptyList(),
    val title: String
)
