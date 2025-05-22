package com.yanakudrinskaya.bookshelf.boarding.domain

interface OnBoardingRepository {
    fun getContent() : List<String>
}