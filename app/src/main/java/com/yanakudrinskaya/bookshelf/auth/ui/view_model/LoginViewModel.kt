package com.yanakudrinskaya.bookshelf.auth.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.auth.ui.models.EditStatus

import com.yanakudrinskaya.bookshelf.auth.ui.models.RequestStatus
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userProfileInteractor: UserProfileInteractor
) : ViewModel() {


    private val requestStatusLiveData = MutableLiveData<RequestStatus>()
    fun getRequestStatusLiveData(): LiveData<RequestStatus> = requestStatusLiveData


    fun processRequest(email: String, password: String) {
            login(email, password)
    }


    private fun login(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch {
                userProfileInteractor.login(email, password).let { result ->
                    when (result) {
                        is Result.Success -> {
                            Log.d("Myregister", "Авторизация прошла успешно")
                            requestStatusLiveData.postValue(
                                RequestStatus.Success
                            )
                        }

                        is Result.Failure -> {
                            Log.d("Myregister", "Ошибка авторизации")
                            requestStatusLiveData.postValue(RequestStatus.Error("Ошибка авторизации"))
                        }
                    }
                }

            }
        } else {
            requestStatusLiveData.postValue(
                RequestStatus.Error("Заполните все поля", EditStatus.PASSWORD)
            )
        }
    }
}
