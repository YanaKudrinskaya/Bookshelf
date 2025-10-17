package com.yanakudrinskaya.bookshelf.data.library.firebase.converters

import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseAuthorDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseWorkAuthorDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.entity.FirebaseWorkEntity
import com.yanakudrinskaya.bookshelf.domain.library.models.Work


class WorkConverter(
    private val authorConverter: AuthorConverter,
) {

    suspend fun toDomain(
        entity: FirebaseWorkEntity,
        workId: String,
        authorDao: FirebaseAuthorDao,
        workAuthorDao: FirebaseWorkAuthorDao
    ): Work {
        val authorIds = workAuthorDao.getAuthorsForWork(workId)
        val authorsWithIds = authorDao.getByIds(authorIds) // Теперь возвращает List<Pair<String, FirebaseAuthorEntity>>

        val authors = authorsWithIds.map { (authorId, authorEntity) ->
            authorConverter.toDomain(authorId, authorEntity) // Передаем ID документа автора
        }
        return Work(
            id = workId,
            authors = authors,
            title = entity.title
        )
    }

    fun toEntity(domain: Work): FirebaseWorkEntity {
        return FirebaseWorkEntity(
            title = domain.title
        )
    }
}
