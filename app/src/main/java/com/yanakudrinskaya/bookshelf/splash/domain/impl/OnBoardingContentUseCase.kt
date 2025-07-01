package com.yanakudrinskaya.bookshelf.splash.domain.impl

import com.yanakudrinskaya.bookshelf.splash.domain.OnBoardingRepository

class OnBoardingContentUseCase(
    private val repository: OnBoardingRepository
) {
    fun getContent() : List<String> {
        return repository.getContent()
    }
}