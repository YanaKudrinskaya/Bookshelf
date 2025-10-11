package com.yanakudrinskaya.bookshelf.library.data.firebase.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.library.data.firebase.entity.FirebaseBookAuthorEntity
import kotlinx.coroutines.tasks.await

class FirebaseBookAuthorDao (
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("book_authors")

    suspend fun insert(bookId: String, authorId: String) {
        collection.document().set(FirebaseBookAuthorEntity(bookId, authorId)).await()
    }

    suspend fun getAuthorsForBook(bookId: String): List<String> {
        return collection.whereEqualTo("bookId", bookId).get().await()
            .toObjects(FirebaseBookAuthorEntity::class.java)
            .map { it.authorId }
    }
}