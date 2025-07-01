package com.yanakudrinskaya.bookshelf.library.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfLocalRepository

class BookshelfLocalRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : BookshelfLocalRepository {



}