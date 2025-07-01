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

class RegisterViewModel (
    private val userProfileInteractor: UserProfileInteractor
) : ViewModel() {

    private val requestStatusLiveData = MutableLiveData<RequestStatus>()
    fun getRequestStatusLiveData(): LiveData<RequestStatus> = requestStatusLiveData

    fun processRequest(name: String, email: String, password: String, confPass: String) {
        register(name, email, password, confPass)

    }

    private fun register(name: String, email: String, password: String, confPass: String) {
        if (name.isEmpty()) {
            requestStatusLiveData.postValue(RequestStatus.Error("Введите ваше имя", EditStatus.NAME))
        } else if (email.isEmpty()) {
            requestStatusLiveData.postValue(RequestStatus.Error("Введите ваш e_mail", EditStatus.EMAIL))
        } else if (password.length < 5) {
            requestStatusLiveData.postValue(RequestStatus.Error("Пароль должен быть не менее 5 символов", EditStatus.PASSWORD))
        } else if (confPass != password) {
            requestStatusLiveData.postValue(RequestStatus.Error("Пароли не совпадают", EditStatus.CONFPASSWORD))
        } else registration(name, email, password)
    }

    private fun registration(name: String, email: String, password: String) {

        viewModelScope.launch {
            userProfileInteractor.register(name, email, password).let { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("Myregister", "Регистрация прошла успешно")
                        requestStatusLiveData.postValue(
                            RequestStatus.Success
                        )
                    }

                    is Result.Failure -> {
                        Log.d("Myregister", "Ошибка: ${result.exception.message}")
                        requestStatusLiveData.postValue(RequestStatus.Error(result.exception.message!!))
                    }
                }
            }
        }
    }
}