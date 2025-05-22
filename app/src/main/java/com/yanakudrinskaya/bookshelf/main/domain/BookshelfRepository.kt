package com.yanakudrinskaya.bookshelf.main.domain

interface BookshelfRepository {
    suspend fun addBookToBookshelf () :Result<Unit>
}