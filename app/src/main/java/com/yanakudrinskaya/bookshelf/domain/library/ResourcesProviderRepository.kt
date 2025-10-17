package com.yanakudrinskaya.bookshelf.domain.library

interface ResourcesProviderRepository {
    fun getString(resId: Int): String
}