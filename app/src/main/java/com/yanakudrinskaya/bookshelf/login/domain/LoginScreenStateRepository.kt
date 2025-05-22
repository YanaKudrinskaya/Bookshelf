package com.yanakudrinskaya.bookshelf.login.domain

import com.yanakudrinskaya.bookshelf.login.domain.models.Content

interface LoginScreenStateRepository {
    fun getLoginContent() : Content
    fun getRegisterContent() : Content
}