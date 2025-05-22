package com.yanakudrinskaya.bookshelf.settings.ui.model


data class UserState(
    val name: String,
    val email: String,
    val filePath: String? = null,
    val placeholder: Int? = null,
)