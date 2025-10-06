package com.yanakudrinskaya.bookshelf.library.domain

import com.yanakudrinskaya.bookshelf.library.domain.models.Author
import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

interface BookshelfRepository {

    fun addBook (book: Book) : Flow<Result<String>>
    fun addWork (work: Work) : Flow<Result<String>>
    fun addBookToBookshelf(bookId: String): Flow<Result<Unit>>
    fun getLibrary() : Flow<Result<List<Book>>>

    fun searchAuthors(query: String): Flow<Result<List<Author>>>
    fun addAuthor(author: Author): Flow<Result<String>>
    fun getAuthorById(authorId: String): Flow<Result<Author>>
    fun searchWorksByAuthor(authorId: String): Flow<Result<List<Work>>>

}