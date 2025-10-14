package com.yanakudrinskaya.bookshelf.splash.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.auth.domain.api.AuthInteractor
import com.yanakudrinskaya.bookshelf.splash.domain.SplashUseCase
import com.yanakudrinskaya.bookshelf.splash.ui.models.NavigationEvent
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authInteractor: AuthInteractor,
    private val splashUseCase: SplashUseCase
) : ViewModel() {

    private val navigationLiveData = MutableLiveData<NavigationEvent>()
    fun getNavigationLiveData(): LiveData<NavigationEvent> = navigationLiveData

    init {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        if (splashUseCase.isFirstLaunch()) {
            navigationLiveData.value = NavigationEvent.FIRST
        } else {
            checkAuth()
        }
    }

    private fun checkAuth() {
        viewModelScope.launch {
            // Используем first() чтобы взять только первый результат
            val result = authInteractor.getCurrentUser().first()
            when (result) {
                is Result.Success -> {
                    navigationLiveData.value = NavigationEvent.MAIN
                }
                is Result.Error -> {
                    navigationLiveData.value = NavigationEvent.LOGIN
                }
            }
        }
    }
}