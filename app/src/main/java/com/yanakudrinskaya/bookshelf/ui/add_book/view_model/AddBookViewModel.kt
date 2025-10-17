package com.yanakudrinskaya.bookshelf.ui.add_book.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.ui.add_book.models.AddBookScreenState
import com.yanakudrinskaya.bookshelf.ui.add_book.models.AddWorkScreenState
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.domain.library.BookshelfInteractor
import com.yanakudrinskaya.bookshelf.domain.library.models.Author
import com.yanakudrinskaya.bookshelf.domain.library.models.Work
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddBookViewModel(
    private val bookshelfInteractor: BookshelfInteractor
) : ViewModel() {

    private val contentList = mutableListOf<Work>()
    private val contentIdList = mutableListOf<String>()

    private val selectedAuthorIds = mutableListOf<String>()
    private val selectedAuthorWork = mutableListOf<Author>()

    private var searchJob: Job? = null

    private val addBookStateLiveData = MutableLiveData<AddBookScreenState>()
    fun getAddBookStateLiveData(): LiveData<AddBookScreenState> = addBookStateLiveData

    private val addWorkStateLiveData = MutableLiveData<AddWorkScreenState>()
    fun getAddWorkStateLiveData(): LiveData<AddWorkScreenState> = addWorkStateLiveData

    fun selectAuthorToWork(author: Author) {
        selectedAuthorWork.add(author)
    }

    fun saveWork(title: String) {
        if (selectedAuthorWork.isEmpty())
            addWorkStateLiveData.value = AddWorkScreenState.AuthorsIsEmpty
        else {
            val work = Work(authors = selectedAuthorWork, title = title)
            searchJob?.cancel()
            searchJob = viewModelScope.launch {

                bookshelfInteractor.addWork(work)
                    .catch { error ->
                        val errorString = error.message
                        Log.d("MyLibrary", errorString.toString())
                    }
                    .collect { result ->
                        when (result) {
                            is Result.Success -> {
                                Log.d("MyLibrary", "Произведение добавлено в базу данных")
                                contentList.add(work)
                                contentIdList.add(result.data!!)
                                addBookStateLiveData.value = AddBookScreenState.Content(contentList)
                            }

                            is Result.Error -> {
                                Log.d("MyLibrary", "Ошибка сохранения")
                            }
                        }
                    }
            }
        }

    }

    fun saveBook(author: String, title: String, publisher: String, year: String) {

        /*if (author.isEmpty()) {
            addBookStateLiveData.value = AddBookScreenState.NavigateFragment(NavigateFragmentEvent.AUTHOR)
            return
        } else if (title.isEmpty() || publisher.isEmpty() || year.isEmpty()) {
            addBookStateLiveData.value = AddBookScreenState.Error("Заполните все поля книги")
            return
        } else {
            addBookStateLiveData.value = AddBookScreenState.Loading
            val authors = bookshelfInteractor.getAuthorById(selectedAuthorId!!)
            val book = Book(
                authors = author,
                title = title,
                publisher = publisher,
                date = year,
                worksId = contentIdList.toList()
            )
            searchJob?.cancel()

            searchJob = viewModelScope.launch {
                bookshelfInteractor.addBook(book).collect { result ->
                    when (result) {
                        is Result.Failure -> addBookStateLiveData.value = AddBookScreenState.Error("Ошибка сохранения")
                        is Result.Success -> addBookStateLiveData.value = AddBookScreenState.Success
                    }
                }
            }
        }*/
    }

    fun searchAuthors(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            bookshelfInteractor.searchAuthors(query)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            // Обновляем UI с результатами поиска
                            addBookStateLiveData.value =
                                AddBookScreenState.AuthorSearchResults(result.data!!)
                        }

                        is Result.Error -> {
                            // Обработка ошибки
                        }
                    }
                }
        }
    }

    fun selectAuthor(author: Author) {
        selectedAuthorIds.add(author.id)
        // Обновляем UI с выбранным автором
        addBookStateLiveData.value = AddBookScreenState.AuthorSelected(author)
    }

    fun searchWorksByAuthor(authorId: String) {
        viewModelScope.launch {
            bookshelfInteractor.searchWorksByAuthor(authorId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            addWorkStateLiveData.value =
                                AddWorkScreenState.WorksSearchResults(result.data!!)
                        }

                        is Result.Error -> {
                            // Обработка ошибки
                        }
                    }
                }
        }
    }

    fun addAuthor(author: Author) {
        viewModelScope.launch {
            bookshelfInteractor.addAuthor(author).collect { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("MyLibrary", "Сохраняем автора ${author.lastName}")
                        selectAuthor(author) // Автоматически выбираем только что добавленного автора
                    }

                    is Result.Error -> {
                        // Обработка ошибки
                    }
                }
            }
        }
    }

}
