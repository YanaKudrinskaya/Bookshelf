package com.yanakudrinskaya.bookshelf.domain.on_boarding

interface OnBoardingRepository {
    fun getContent() : List<String>
}