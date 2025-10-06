package com.yanakudrinskaya.bookshelf.library.data.firebase.dao

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.library.data.firebase.entity.FirebaseBookEntity
import kotlinx.coroutines.tasks.await

class FirebaseBookDao (
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("books")

    suspend fun insert(book: FirebaseBookEntity): String {
        val docRef = collection.document()
        docRef.set(book).await()
        return docRef.id
    }

    suspend fun getById(id: String): Pair<String, FirebaseBookEntity>? {
        val document = collection.document(id).get().await()
        return if (document.exists()) {
            document.id to document.toObject(FirebaseBookEntity::class.java)!!
        } else {
            null
        }
    }

    suspend fun getByIds(ids: List<String>):List<Pair<String, FirebaseBookEntity>>   {
        if (ids.isEmpty()) return emptyList()
        return collection.whereIn(FieldPath.documentId(), ids).get().await()
            .documents
            .map { document ->
                document.id to document.toObject(FirebaseBookEntity::class.java)!!
            }
    }
}