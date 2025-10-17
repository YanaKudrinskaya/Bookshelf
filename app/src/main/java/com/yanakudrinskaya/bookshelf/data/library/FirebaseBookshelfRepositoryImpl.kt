package com.yanakudrinskaya.bookshelf.data.library

import android.util.Log
import com.yanakudrinskaya.bookshelf.data.library.firebase.converters.AuthorConverter
import com.yanakudrinskaya.bookshelf.data.library.firebase.converters.BookConverter
import com.yanakudrinskaya.bookshelf.data.library.firebase.converters.WorkConverter
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseAuthorDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseBookAuthorDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseBookDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseBookWorkDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseBookshelfDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseWorkAuthorDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseWorkDao
import com.yanakudrinskaya.bookshelf.domain.profile.api.LocalUserRepository
import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.domain.library.BookshelfRepository
import com.yanakudrinskaya.bookshelf.domain.library.models.Author
import com.yanakudrinskaya.bookshelf.domain.library.models.Book
import com.yanakudrinskaya.bookshelf.domain.library.models.Work
import com.yanakudrinskaya.bookshelf.utils.ResponseStatus
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FirebaseBookshelfRepositoryImpl(
    private val userProfile: LocalUserRepository,
    private val bookDao: FirebaseBookDao,
    private val authorDao: FirebaseAuthorDao,
    private val workDao: FirebaseWorkDao,
    private val bookAuthorDao: FirebaseBookAuthorDao,
    private val bookWorkDao: FirebaseBookWorkDao,
    private val bookshelfDao: FirebaseBookshelfDao,
    private val bookConverter: BookConverter,
    private val authorConverter: AuthorConverter,
    private val workConverter: WorkConverter,
    private val workAuthorDao: FirebaseWorkAuthorDao
) : BookshelfRepository {

    private suspend fun getCurrentUser(): Result<User> {
        return try {
            userProfile.getLocalUserProfileStream()
                .first { it is Result.Success }
        } catch (e: Exception) {
            Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to get current user: ${e.message}")
        }
    }

    private suspend fun getCurrentBookshelfId(): String {
        return when (val result = getCurrentUser()) {
            is Result.Success -> result.data.bookshelfId
            is Result.Error -> throw Exception("User not available: ${result.message}")
            else -> throw Exception("Unexpected user state")
        }
    }

    override fun addBook(book: Book): Flow<Result<String>> = flow {
        try {
            val bookEntity = bookConverter.toEntity(book)
            val bookId = bookDao.insert(bookEntity)

            book.authors.forEach { author ->
                val authorId = author.id.ifEmpty {
                    authorDao.insert(authorConverter.toEntity(author))
                }
                bookAuthorDao.insert(bookId, authorId)
            }

            book.works.forEach { work ->
                val workId = work.id.ifEmpty {
                    workDao.insert(workConverter.toEntity(work))
                }
                bookWorkDao.insert(bookId, workId)
            }

            emit(Result.Success(bookId))
        } catch (e: Exception) {
            emit(Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to add book: ${e.message}"))
        }
    }

    override fun addWork(work: Work): Flow<Result<String>> = flow {
        try {
            val workEntity = workConverter.toEntity(work)
            val workId = workDao.insert(workEntity)
            emit(Result.Success(workId))
        } catch (e: Exception) {
            emit(Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to add work: ${e.message}"))
        }
    }

    override fun addBookToBookshelf(bookId: String): Flow<Result<Unit>> = flow {
        try {
            val bookshelfId = getCurrentBookshelfId()
            bookshelfDao.addBook(bookshelfId, bookId)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to add book to bookshelf: ${e.message}"))
        }
    }

    override fun getLibrary(): Flow<Result<List<Book>>> = flow {
        try {
            val bookshelfId = getCurrentBookshelfId()
            val bookIds = bookshelfDao.getBooks(bookshelfId)
            val bookEntities = bookDao.getByIds(bookIds)

            val books = bookEntities.map { (id, entity) ->
                bookConverter.toDomain(
                    entity,
                    id,
                    authorDao,
                    workDao,
                    bookAuthorDao,
                    bookWorkDao,
                    workAuthorDao
                )
            }

            emit(Result.Success(books))
        } catch (e: Exception) {
            emit(Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to get library: ${e.message}"))
        }
    }

    override fun searchAuthors(query: String): Flow<Result<List<Author>>> = flow {
        try {
            val authorEntities = authorDao.search(query)
            val authors = authorEntities.map { authorConverter.toDomain(it.first, it.second) }
            emit(Result.Success(authors))
        } catch (e: Exception) {
            emit(Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to search authors: ${e.message}"))
        }
    }

    override fun addAuthor(author: Author): Flow<Result<String>> = flow {
        try {
            Log.d("MyLibrary", "Сохраняем автора ${author.lastName}")
            val authorEntity = authorConverter.toEntity(author)
            val authorId = authorDao.insert(authorEntity)
            emit(Result.Success(authorId))
        } catch (e: Exception) {
            emit(Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to add author: ${e.message}"))
        }
    }

    override fun getAuthorById(authorId: String): Flow<Result<Author>> = flow {
        try {
            val author = authorDao.getById(authorId)
                ?: return@flow emit(Result.Error(ResponseStatus.NOT_FOUND, "Author not found"))
            emit(Result.Success(authorConverter.toDomain(author.first, author.second)))
        } catch (e: Exception) {
            emit(Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to get author: ${e.message}"))
        }
    }

    override fun searchWorksByAuthor(authorId: String): Flow<Result<List<Work>>> = flow {
        try {
            val workIds = workAuthorDao.getWorksByAuthor(authorId)
            val workEntities = workDao.getByIds(workIds)
            val works = workEntities.map { (id, entity) ->
                workConverter.toDomain(
                    entity,
                    id,
                    authorDao,
                    workAuthorDao
                )
            }
            emit(Result.Success(works))
        } catch (e: Exception) {
            emit(Result.Error(ResponseStatus.UNKNOWN_ERROR, "Failed to search works: ${e.message}"))
        }
    }
}