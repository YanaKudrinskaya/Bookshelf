package com.yanakudrinskaya.bookshelf.login.data

import android.content.Context
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.login.domain.LoginScreenStateRepository
import com.yanakudrinskaya.bookshelf.login.domain.models.Content

class LoginScreenStateRepositoryImpl(
    private val context: Context
) : LoginScreenStateRepository {

    override fun getLoginContent(): Content {
        return Content (
            title = context.getString(R.string.welcome_login),
            button = context.getString(R.string.login_btn),
            bottomButton = context.getString(R.string.register)
        )
    }

    override fun getRegisterContent(): Content {
        return Content (
            title = context.getString(R.string.register),
            button = context.getString(R.string.register_btn),
            bottomButton = context.getString(R.string.login_btn)
        )
    }
}