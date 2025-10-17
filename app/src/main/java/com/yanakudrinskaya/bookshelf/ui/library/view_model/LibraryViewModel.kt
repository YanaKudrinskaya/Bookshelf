package com.yanakudrinskaya.bookshelf.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.domain.library.BookshelfInteractor
import com.yanakudrinskaya.bookshelf.ui.library.model.LibraryState
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val bookshelfInteractor: BookshelfInteractor
) : ViewModel() {

    private var searchJob: Job? = null

    private val libraryLiveData = MutableLiveData<LibraryState>()
    fun getLibraryLiveData(): LiveData<LibraryState> = mediatorLibraryLiveData


    fun loadLibrary() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            renderState(LibraryState.Loading)
            bookshelfInteractor
                .getLibrary()
                .collect { result ->
                    when (result) {
                        is Result.Error -> renderState(LibraryState.Error)
                        is Result.Success -> {
                            if (result.data.isEmpty()) renderState(LibraryState.Empty)
                            else renderState(LibraryState.Content(result.data))
                        }
                    }
                }
        }
    }

    private fun renderState(state: LibraryState) {
        libraryLiveData.postValue(state)
    }

    private val mediatorLibraryLiveData = MediatorLiveData<LibraryState>().also { liveData ->
        liveData.addSource(libraryLiveData) { libraryState ->
            liveData.value = when (libraryState) {
                is LibraryState.Content -> LibraryState.Content(libraryState.books)
                is LibraryState.Empty -> libraryState
                is LibraryState.Error -> libraryState
                is LibraryState.Loading -> libraryState
            }
        }
    }
}