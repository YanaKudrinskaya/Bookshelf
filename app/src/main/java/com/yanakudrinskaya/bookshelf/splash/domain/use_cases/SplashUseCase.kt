package com.yanakudrinskaya.bookshelf.splash.domain.use_cases

import com.yanakudrinskaya.bookshelf.splash.domain.SplashRepository

class SplashUseCase(
    private val repository: SplashRepository
) {
    fun isFirstLaunch(): Boolean {
        return repository.isFirstLaunch()
    }
}