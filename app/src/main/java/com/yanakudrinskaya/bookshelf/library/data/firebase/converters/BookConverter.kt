package com.yanakudrinskaya.bookshelf.library.data.firebase.converters

import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseAuthorDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseBookAuthorDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseBookWorkDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseWorkAuthorDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseWorkDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.entity.FirebaseBookEntity
import com.yanakudrinskaya.bookshelf.library.domain.models.Book

class BookConverter(
    private val authorConverter: AuthorConverter,
    private val workConverter: WorkConverter
) {
    suspend fun toDomain(
        bookEntity: FirebaseBookEntity,
        bookId: String,
        authorDao: FirebaseAuthorDao,
        workDao: FirebaseWorkDao,
        bookAuthorDao: FirebaseBookAuthorDao,
        bookWorkDao: FirebaseBookWorkDao,
        workAuthorDao: FirebaseWorkAuthorDao
    ): Book {
        // Получаем все связи за один запрос
        val authorIds = bookAuthorDao.getAuthorsForBook(bookId)
        val workIds = bookWorkDao.getWorksForBook(bookId)

        // Параллельно загружаем авторов и произведения
        val authorsWithIds = authorDao.getByIds(authorIds).map { (authorId, authorEntity) ->
            authorConverter.toDomain(authorId, authorEntity)
        }
        val worksWithIds = workDao.getByIds(workIds).map { (woorkId, workEntity) ->
            workConverter.toDomain(
                workEntity,
                woorkId,
                authorDao,
                workAuthorDao
            )
        }
        return Book(
            id = bookId,
            title = bookEntity.title,
            authors = authorsWithIds,
            publisher = bookEntity.publisher,
            date = bookEntity.date,
            works = worksWithIds
        )
    }

    fun toEntity(domain: Book): FirebaseBookEntity {
        return FirebaseBookEntity(
            title = domain.title,
            publisher = domain.publisher,
            date = domain.date

        )
    }
}