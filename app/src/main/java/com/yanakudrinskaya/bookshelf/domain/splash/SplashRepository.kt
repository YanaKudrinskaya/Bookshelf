package com.yanakudrinskaya.bookshelf.domain.splash

interface SplashRepository {
    fun isFirstLaunch(): Boolean
}