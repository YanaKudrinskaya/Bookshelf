package com.yanakudrinskaya.bookshelf.data.library.firebase.dao

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseBookshelfDao (
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("bookshelves")

    suspend fun addBook(bookshelfId: String, bookId: String) {
        val docRef = collection.document(bookshelfId)
        firestore.runTransaction { transaction ->
            val doc = transaction.get(docRef)
            val currentBooks = doc.get("books") as? List<String> ?: emptyList()
            if (!currentBooks.contains(bookId)) {
                transaction.update(docRef, "books", currentBooks + bookId)
            }
        }.await()
    }

    suspend fun getBooks(bookshelfId: String): List<String> {
        return collection.document(bookshelfId).get().await()
            .get("books") as? List<String> ?: emptyList()
    }
}