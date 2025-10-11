package com.yanakudrinskaya.bookshelf.on_boarding.domain

interface OnBoardingRepository {
    fun getContent() : List<String>
}