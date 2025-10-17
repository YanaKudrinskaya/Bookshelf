package com.yanakudrinskaya.bookshelf.domain.library

import com.yanakudrinskaya.bookshelf.domain.library.models.Author
import com.yanakudrinskaya.bookshelf.domain.library.models.Book
import com.yanakudrinskaya.bookshelf.domain.library.models.Work
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow


interface BookshelfInteractor {

    fun addBook(book: Book): Flow<Result<String>>
    fun addWork(work: Work): Flow<Result<String>>
    fun getLibrary(): Flow<Result<List<Book>>>

    fun searchAuthors(query: String): Flow<Result<List<Author>>>
    fun addAuthor(author: Author): Flow<Result<String>>
    fun getAuthorById(authorId: String): Flow<Result<Author>>
    fun searchWorksByAuthor(authorId: String): Flow<Result<List<Work>>>

}