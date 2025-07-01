package com.yanakudrinskaya.bookshelf.library.domain

import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import com.yanakudrinskaya.bookshelf.utils.Result

interface BookshelfRepository {

    suspend fun addBook (book: Book) : Result<String>
    suspend fun addWork (work: Work) : Result<String>
    suspend fun addBookToBookshelf(bookId: String): Result<Unit>
    suspend fun getLibrary() : Result<List<Book>>

}