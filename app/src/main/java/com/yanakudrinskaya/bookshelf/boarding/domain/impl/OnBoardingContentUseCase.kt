package com.yanakudrinskaya.bookshelf.boarding.domain.impl

import com.yanakudrinskaya.bookshelf.boarding.domain.OnBoardingRepository

class OnBoardingContentUseCase(
    private val repository: OnBoardingRepository
) {
    fun getContent() : List<String> {
        return repository.getContent()
    }
}