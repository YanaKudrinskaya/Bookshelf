package com.yanakudrinskaya.bookshelf.splash.domain

interface OnBoardingRepository {
    fun getContent() : List<String>
}