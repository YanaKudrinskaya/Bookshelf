package com.yanakudrinskaya.bookshelf.library.domain

interface ResourcesProviderRepository {
    fun getString(resId: Int): String
}