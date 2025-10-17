package com.yanakudrinskaya.bookshelf.domain.splash

class SplashUseCase(
    private val repository: SplashRepository
) {
    fun isFirstLaunch(): Boolean {
        return repository.isFirstLaunch()
    }
}