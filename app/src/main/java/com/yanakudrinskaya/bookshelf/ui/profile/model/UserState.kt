package com.yanakudrinskaya.bookshelf.ui.profile.model


data class UserState(
    val name: String,
    val email: String,
    val filePath: String? = null,
    val placeholder: Int? = null,
    val avatarIsChange: Boolean = false
)