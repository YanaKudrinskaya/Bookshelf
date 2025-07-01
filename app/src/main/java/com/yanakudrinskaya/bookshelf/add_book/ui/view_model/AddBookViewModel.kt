package com.yanakudrinskaya.bookshelf.add_book.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.add_book.ui.models.BookState
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.add_book.ui.models.DialogEvent
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfInteractor
import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import com.yanakudrinskaya.bookshelf.root.ui.model.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddBookViewModel(
    private val bookshelfInteractor: BookshelfInteractor
) : ViewModel() {

    private val contentList = mutableListOf<Work>()
    private val contentIdList = mutableListOf<String>()

    private var searchJob: Job? = null

    private val contentLiveData = MutableLiveData<MutableList<Work>>()
    fun getContentLiveData(): LiveData<MutableList<Work>> = contentLiveData

    private val dialogLiveData = SingleLiveEvent<DialogEvent>()
    fun getDialogLiveData(): SingleLiveEvent<DialogEvent> = dialogLiveData

    private val addBookLiveData = MutableLiveData<BookState>()
    fun getAddBookLiveData(): LiveData<BookState> = addBookLiveData

    fun saveWork(work: Work) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            bookshelfInteractor.addWork(work).collect { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("MyLibrary", "Произведение добавлено в базу данных")
                        contentList.add(work)
                        contentIdList.add(result.data)
                        contentLiveData.value = contentList
                    }

                    is Result.Failure -> {
                        Log.d("MyLibrary", "Ошибка сохранения")
                    }
                }
            }
        }
    }

    fun saveBook(author: String, title: String, publisher: String, year: String) {

        if (author.isEmpty()) {
            dialogLiveData.value = DialogEvent.AUTHOR
            return
        } else if (title.isEmpty() || publisher.isEmpty() || year.isEmpty()) {
            dialogLiveData.value = DialogEvent.TOAST
            return
        } else {
            addBookLiveData.value = BookState.Loading
            val book = Book(
                author = author,
                title = title,
                publisher = publisher,
                date = year,
                worksId = contentIdList.toList()
            )
            searchJob?.cancel()

            searchJob = viewModelScope.launch {
                bookshelfInteractor.addBook(book).collect { result ->
                    when (result) {
                        is Result.Failure -> addBookLiveData.value = BookState.Error("Ошибка сохранения")
                        is Result.Success -> addBookLiveData.value = BookState.Success
                    }
                }
            }
        }
    }

}
