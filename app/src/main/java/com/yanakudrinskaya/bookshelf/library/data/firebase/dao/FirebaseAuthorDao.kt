package com.yanakudrinskaya.bookshelf.library.data.firebase.dao

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.library.data.firebase.entity.FirebaseAuthorEntity
import kotlinx.coroutines.tasks.await

class FirebaseAuthorDao(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("authors")

    suspend fun insert(author: FirebaseAuthorEntity): String {
        val docRef = collection.document()
        docRef.set(author).await()
        return docRef.id
    }

    suspend fun getById(id: String): Pair<String, FirebaseAuthorEntity>? {
        val document = collection.document(id).get().await()
        return if (document.exists()) {
            document.id to document.toObject(FirebaseAuthorEntity::class.java)!!
        } else {
            null
        }
    }

    suspend fun search(query: String): List<Pair<String, FirebaseAuthorEntity>> {
        return collection
            .orderBy("lastName")
            .startAt(query)
            .endAt("$query\uf8ff")
            .limit(10)
            .get()
            .await()
            .documents
            .map { document ->
                // Возвращаем пару (ID документа, сущность)
                document.id to document.toObject(FirebaseAuthorEntity::class.java)!!
            }
    }

    suspend fun getByIds(ids: List<String>): List<Pair<String, FirebaseAuthorEntity>> {
        if (ids.isEmpty()) return emptyList()
        return collection.whereIn(FieldPath.documentId(), ids).get().await()
            .documents
            .map { document ->
                document.id to document.toObject(FirebaseAuthorEntity::class.java)!!
            }
    }
}