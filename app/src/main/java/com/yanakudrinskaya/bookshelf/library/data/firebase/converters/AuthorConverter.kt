package com.yanakudrinskaya.bookshelf.library.data.firebase.converters

import com.yanakudrinskaya.bookshelf.library.data.firebase.entity.FirebaseAuthorEntity
import com.yanakudrinskaya.bookshelf.library.domain.models.Author

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