package com.yanakudrinskaya.bookshelf.root.ui.view_model

import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.root.ui.model.SingleLiveEvent

class RootViewModel  : ViewModel() {

    private val navigationEvents = SingleLiveEvent<Boolean>()
    fun getNavigationEvents(): SingleLiveEvent<Boolean> = navigationEvents

    fun changeDestination(destination: Int) {
        navigationEvents.value = (destination == R.id.libraryFragment || destination == R.id.wishFragment || destination == R.id.profileFragment)
    }
}