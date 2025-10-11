package com.yanakudrinskaya.bookshelf.splash.domain

class SplashUseCase(
    private val repository: SplashRepository
) {
    fun isFirstLaunch(): Boolean {
        return repository.isFirstLaunch()
    }
}