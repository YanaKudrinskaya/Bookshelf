package com.yanakudrinskaya.bookshelf.splash.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.Result
import com.yanakudrinskaya.bookshelf.login.domain.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.splash.domain.use_cases.SplashUseCase
import com.yanakudrinskaya.bookshelf.splash.ui.model.NavigationEvent
import kotlinx.coroutines.launch

class SplashViewModel (
    private val userProfileInteractor: UserProfileInteractor,
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
            userProfileInteractor.getCurrentUser().let { result ->
                when (result) {
                    is Result.Success -> {
                        navigationLiveData.value = NavigationEvent.MAIN
                    }
                    is Result.Failure -> {
                        Log.d("Myregister", "Ошибка ${result.exception}")
                        navigationLiveData.value = NavigationEvent.LOGIN
                    }
                }
            }
        }
    }
}