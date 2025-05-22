package com.yanakudrinskaya.bookshelf.login.domain.impl

import com.yanakudrinskaya.bookshelf.login.domain.LoginScreenStateInteractor
import com.yanakudrinskaya.bookshelf.login.domain.LoginScreenStateRepository
import com.yanakudrinskaya.bookshelf.login.domain.models.Content

class LoginScreenStateInteractorImpl(
    private val repository: LoginScreenStateRepository,
) : LoginScreenStateInteractor {

    override fun getLoginContent(): Content {
        return repository.getLoginContent()
    }

    override fun getRegisterContent(): Content {
        return repository.getRegisterContent()
    }
}