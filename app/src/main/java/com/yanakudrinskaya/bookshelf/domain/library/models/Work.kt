package com.yanakudrinskaya.bookshelf.domain.library.models

data class Work (
    val id: String = "",
    val authors: List<Author> = emptyList(),
    val title: String
)
