package com.yanakudrinskaya.bookshelf.data.library.firebase.converters

import com.yanakudrinskaya.bookshelf.data.library.firebase.entity.FirebaseAuthorEntity
import com.yanakudrinskaya.bookshelf.domain.library.models.Author

class AuthorConverter {
    fun toDomain(id: String, entity: FirebaseAuthorEntity): Author {
        return Author(
            id = id,
            lastName = entity.lastName,
            firstName = entity.firstName,
            middleName = entity.middleName
        )
    }

    fun toEntity(domain: Author): FirebaseAuthorEntity {
        return FirebaseAuthorEntity(
            lastName = domain.lastName,
            firstName = domain.firstName,
            middleName = domain.middleName
        )
    }
}