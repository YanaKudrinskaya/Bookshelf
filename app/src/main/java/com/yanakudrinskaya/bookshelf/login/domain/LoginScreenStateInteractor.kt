package com.yanakudrinskaya.bookshelf.login.domain

import com.yanakudrinskaya.bookshelf.login.domain.models.Content

interface LoginScreenStateInteractor {
    fun getLoginContent() : Content
    fun getRegisterContent() : Content
}