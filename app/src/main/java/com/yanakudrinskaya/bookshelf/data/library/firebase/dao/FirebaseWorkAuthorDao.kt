package com.yanakudrinskaya.bookshelf.data.library.firebase.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.data.library.firebase.entity.FirebaseWorkAuthorEntity
import kotlinx.coroutines.tasks.await

class FirebaseWorkAuthorDao(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("work_authors")

    suspend fun insert(workId: String, authorId: String) {
        collection.document().set(FirebaseWorkAuthorEntity(workId, authorId)).await()
    }

    suspend fun getAuthorsForWork(workId: String): List<String> {
        return collection.whereEqualTo("workId", workId).get().await()
            .toObjects(FirebaseWorkAuthorEntity::class.java)
            .map { it.authorId }
    }

    suspend fun getWorksByAuthor(authorId: String) : List<String> {
        return collection.whereEqualTo("authorId", authorId).get().await()
            .toObjects(FirebaseWorkAuthorEntity::class.java)
            .map { it.workId }
    }
}