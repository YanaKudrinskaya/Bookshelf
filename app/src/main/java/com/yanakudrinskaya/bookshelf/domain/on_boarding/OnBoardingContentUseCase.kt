package com.yanakudrinskaya.bookshelf.domain.on_boarding

class OnBoardingContentUseCase(
    private val repository: OnBoardingRepository
) {
    fun getContent() : List<String> {
        return repository.getContent()
    }
}