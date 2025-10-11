package com.yanakudrinskaya.bookshelf.library.data.firebase.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.library.data.firebase.entity.FirebaseBookWorkEntity
import kotlinx.coroutines.tasks.await

class FirebaseBookWorkDao (
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("book_works")

    suspend fun insert(bookId: String, workId: String) {
        collection.document().set(FirebaseBookWorkEntity(bookId, workId)).await()
    }

    suspend fun getWorksForBook(bookId: String): List<String> {
        return collection.whereEqualTo("bookId", bookId).get().await()
            .toObjects(FirebaseBookWorkEntity::class.java)
            .map { it.workId }
    }
}