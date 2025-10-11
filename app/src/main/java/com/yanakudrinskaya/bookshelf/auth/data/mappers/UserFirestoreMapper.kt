package com.yanakudrinskaya.bookshelf.auth.data.mappers

import com.yanakudrinskaya.bookshelf.auth.domain.models.User

class UserFirestoreMapper {

    companion object {
        private const val FIELD_ID = "id"
        private const val FIELD_NAME = "name"
        private const val FIELD_EMAIL = "email"
        private const val FIELD_BOOKSHELF_ID = "bookshelfId"
        private const val FIELD_READ_BOOKS = "readBooks"
    }

    fun userToFirestoreMap(user: User): Map<String, Any> {
        return mapOf(
            FIELD_ID to user.userId,
            FIELD_NAME to user.name,
            FIELD_EMAIL to user.email,
            FIELD_BOOKSHELF_ID to user.bookshelfId,
            FIELD_READ_BOOKS to user.readBooks
        )
    }

    fun documentToUser(document: Map<String, Any?>, userId: String): User {
        return User(
            userId = userId,
            name = document[FIELD_NAME] as? String ?: "",
            email = document[FIELD_EMAIL] as? String ?: "",
            bookshelfId = document[FIELD_BOOKSHELF_ID] as? String ?: "",
            readBooks = document[FIELD_READ_BOOKS] as? List<String> ?: emptyList()
        )
    }
}