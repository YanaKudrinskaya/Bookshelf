package com.yanakudrinskaya.bookshelf.library.data

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.yanakudrinskaya.bookshelf.auth.domain.AuthRepository
import com.yanakudrinskaya.bookshelf.auth.domain.models.UserCurrent
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfRepository
import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.tasks.await

class BookshelfRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : BookshelfRepository {

    override suspend fun addBook(book: Book): Result<String> {
        return try {
            val bookRef = firestore.collection("books").document()
            val bookId = bookRef.id

            firestore.runTransaction { transaction ->
                transaction.set(
                    bookRef,
                    mapOf(
                        "author" to book.author,
                        "title" to book.title,
                        "publisher" to book.publisher,
                        "date" to book.date,
                        "worksId" to book.worksId,
                    )
                )
            }.await()

            Result.Success(bookId)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }


    override suspend fun addWork(work: Work): Result<String> {
        return try {
            val workRef = firestore.collection("works").document()
            val workId = workRef.id

            firestore.runTransaction { transaction ->
                transaction.set(
                    workRef,
                    mapOf(
                        "author" to work.author,
                        "title" to work.title
                    )
                )
            }.await()

            Result.Success(workId)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun addBookToBookshelf(bookId: String): Result<Unit> {
        return try {

            // Получаем текущий список книг в bookshelf
            val bookshelfRef = firestore.collection("bookshelves").document(UserCurrent.bookshelfId)
            val bookshelfDoc = bookshelfRef.get().await()
            val currentBookIds = bookshelfDoc.get("bookIds") as? List<String> ?: emptyList()

            // Проверяем, есть ли уже такая книга в bookshelf
            if (currentBookIds.contains(bookId)) {
                return Result.Failure(Exception("Book already exists in bookshelf"))
            }

            // Обновляем bookshelf, добавляя новую книгу
            firestore.runTransaction { transaction ->
                val newBookIds = currentBookIds + bookId
                transaction.update(bookshelfRef, "bookIds", newBookIds)
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getLibrary(): Result<List<Book>> {
        //Получаю список id книг из bookshelf
        return try {
            // Получаем bookshelf пользователя
            val bookshelfRef = firestore.collection("bookshelves").document(UserCurrent.bookshelfId)
            val bookshelfDoc = bookshelfRef.get().await()

            // Получаем список ID книг из bookshelf
            val bookIds = bookshelfDoc.get("bookIds") as? List<String> ?: emptyList()

            // Если список пуст, возвращаем пустой результат
            if (bookIds.isEmpty()) {
                return Result.Success(emptyList())
            }

            // Получаем все книги по их ID
            val booksQuery = firestore.collection("books")
                .whereIn(FieldPath.documentId(), bookIds)
                .get()
                .await()

            // Преобразуем документы в объекты Book
            val books = booksQuery.documents.mapNotNull { document ->
                try {
                    Book(
                        id = document.id,
                        author = document.getString("author") ?: "",
                        title = document.getString("title") ?: "",
                        publisher = document.getString("publisher") ?: "",
                        date = document.getString("date") ?: "",
                        worksId = document.get("worksId") as? List<String> ?: emptyList()
                    )
                } catch (e: Exception) {
                    null // Пропускаем некорректные документы
                }
            }

            Result.Success(books)
        } catch (e: Exception) {
            Result.Failure(e)
        }

    }

}