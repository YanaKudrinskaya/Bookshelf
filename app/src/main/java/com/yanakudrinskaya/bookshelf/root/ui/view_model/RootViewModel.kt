package com.yanakudrinskaya.bookshelf.root.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class RootViewModel : ViewModel() {

    private val navigationEvents = MutableLiveData<Boolean>(true)
    fun getNavigationEvents(): LiveData<Boolean> = navigationEvents

    fun setNavigationVisible(visible: Boolean) {
        navigationEvents.value = visible
    }
}