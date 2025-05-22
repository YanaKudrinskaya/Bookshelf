package com.yanakudrinskaya.bookshelf.login.ui.models

import com.yanakudrinskaya.bookshelf.login.domain.models.Content


data class LoginScreenState (
    val screenState: ScreenState,
    val content: Content? = null
)

enum class ScreenState { LOGIN, REGISTER }