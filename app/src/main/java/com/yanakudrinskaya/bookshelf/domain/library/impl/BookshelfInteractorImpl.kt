package com.yanakudrinskaya.bookshelf.domain.library.impl

import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.domain.library.BookshelfInteractor
import com.yanakudrinskaya.bookshelf.domain.library.BookshelfRepository
import com.yanakudrinskaya.bookshelf.domain.library.models.Author
import com.yanakudrinskaya.bookshelf.domain.library.models.Book
import com.yanakudrinskaya.bookshelf.domain.library.models.Work
import kotlinx.coroutines.flow.Flow

class BookshelfInteractorImpl(
    private val bookshelfRepository: BookshelfRepository
) : BookshelfInteractor {


    override fun addBook(book: Book): Flow<Result<String>> {
        return bookshelfRepository.addBook(book)
    }

    override fun addWork(work: Work): Flow<Result<String>> {
        return bookshelfRepository.addWork(work)
    }

    override fun getLibrary(): Flow<Result<List<Book>>> {
        return bookshelfRepository.getLibrary()
    }

    override fun searchAuthors(query: String): Flow<Result<List<Author>>> {
        return bookshelfRepository.searchAuthors(query)
    }

    override fun addAuthor(author: Author): Flow<Result<String>> {
        return bookshelfRepository.addAuthor(author)
    }

    override fun getAuthorById(authorId: String): Flow<Result<Author>> {
        return bookshelfRepository.getAuthorById(authorId)
    }

    override fun searchWorksByAuthor(authorId: String): Flow<Result<List<Work>>> {
        return bookshelfRepository.searchWorksByAuthor(authorId)
    }

}