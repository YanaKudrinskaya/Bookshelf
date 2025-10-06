package com.yanakudrinskaya.bookshelf.library.data.firebase.dao

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.library.data.firebase.entity.FirebaseWorkEntity
import kotlinx.coroutines.tasks.await

class FirebaseWorkDao (
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("works")

    suspend fun insert(work: FirebaseWorkEntity): String {
        val docRef = collection.document()
        docRef.set(work).await()
        return docRef.id
    }

    suspend fun getById(id: String): Pair<String, FirebaseWorkEntity>? {
        val document = collection.document(id).get().await()
        return if (document.exists()) {
            document.id to document.toObject(FirebaseWorkEntity::class.java)!!
        } else {
            null
        }
    }

    suspend fun getByIds(ids: List<String>): List<Pair<String, FirebaseWorkEntity>> {
        if (ids.isEmpty()) return emptyList()
        return collection.whereIn(FieldPath.documentId(), ids).get().await()
            .documents
            .map { document ->
                document.id to document.toObject(FirebaseWorkEntity::class.java)!!
            }
    }
}