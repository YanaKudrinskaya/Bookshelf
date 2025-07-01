package com.yanakudrinskaya.bookshelf.library.domain

import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import kotlinx.coroutines.flow.Flow


interface BookshelfInteractor {

    fun addBook(book: Book): Flow<Result<Unit>>
    fun addWork(work: Work): Flow<Result<String>>
    fun getLibrary(): Flow<Result<List<Book>>>

}