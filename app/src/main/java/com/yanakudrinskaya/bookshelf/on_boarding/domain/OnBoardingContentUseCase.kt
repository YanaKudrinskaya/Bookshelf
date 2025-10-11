package com.yanakudrinskaya.bookshelf.on_boarding.domain

class OnBoardingContentUseCase(
    private val repository: OnBoardingRepository
) {
    fun getContent() : List<String> {
        return repository.getContent()
    }
}