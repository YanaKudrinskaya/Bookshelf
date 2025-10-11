package com.yanakudrinskaya.bookshelf.splash.domain

interface SplashRepository {
    fun isFirstLaunch(): Boolean
}