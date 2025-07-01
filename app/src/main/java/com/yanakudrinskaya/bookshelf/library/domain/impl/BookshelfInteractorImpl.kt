package com.yanakudrinskaya.bookshelf.library.domain.impl

import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfInteractor
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfRepository
import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BookshelfInteractorImpl(
    private val bookshelfRepository: BookshelfRepository
) : BookshelfInteractor {


    override fun addBook(book: Book): Flow<Result<Unit>> = flow {
        try {
            // 1. Добавляем книгу в коллекцию books
            when (val addResult = bookshelfRepository.addBook(book)) {
                is Result.Success -> {
                    // 2. Добавляем ID книги в bookshelf пользователя
                    val bookId = addResult.data
                    when (val bookshelfResult = bookshelfRepository.addBookToBookshelf(bookId)) {
                        is Result.Success -> emit(Result.Success(Unit))
                        is Result.Failure -> emit(bookshelfResult)
                        else -> emit(Result.Failure(IllegalStateException("Unknown result type")))
                    }
                }
                is Result.Failure -> emit(addResult)
                else -> emit(Result.Failure(IllegalStateException("Unknown result type")))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun addWork(work: Work): Flow<Result<String>> = flow {
        try {
            val result = bookshelfRepository.addWork(work)
            emit(result)
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun getLibrary(): Flow<Result<List<Book>>> = flow {
        try {
            val result = bookshelfRepository.getLibrary()
            emit(result)
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

}