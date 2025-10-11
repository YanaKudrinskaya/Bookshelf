package com.yanakudrinskaya.bookshelf.auth.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.auth.domain.AuthInteractor
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.auth.ui.models.RequestStatus
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val requestStatusLiveData = MutableLiveData<RequestStatus>()
    fun getRequestStatusLiveData(): LiveData<RequestStatus> = requestStatusLiveData

    private val fieldErrorsLiveData = MutableLiveData<Pair<String, String>?>()
    fun getFieldErrorsLiveData(): LiveData<Pair<String, String>?> = fieldErrorsLiveData

    private val loadingLiveData = MutableLiveData<Boolean>()
    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData

    fun processRequest(email: String, password: String) {
        if (validateFields(email, password)) {
            login(email, password)
        }
    }

    private fun validateFields(email: String, password: String): Boolean {
        val emailError = if (email.isEmpty()) "Введите email" else null
        val passwordError = if (password.isEmpty()) "Введите пароль" else null

        if (emailError != null || passwordError != null) {
            fieldErrorsLiveData.postValue(Pair(emailError ?: "", passwordError ?: ""))
            return false
        }

//        // Дополнительная валидация email формата
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            fieldErrorsLiveData.postValue(Pair("Неверный формат email", ""))
//            return false
//        }

        fieldErrorsLiveData.postValue(null)
        return true
    }

    private fun login(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch {
                authInteractor.login(email, password).let { result ->
                    when (result) {
                        is Result.Success -> {
                            requestStatusLiveData.postValue(
                                RequestStatus.Success
                            )
                        }

                        is Result.Error -> {
                            requestStatusLiveData.postValue(RequestStatus.Error("Ошибка авторизации"))
                        }
                    }
                }

            }
        } else {
            requestStatusLiveData.postValue(
                RequestStatus.Error("Заполните все поля")
            )
        }
    }

    fun signInWithGoogle(idToken: String) {
        loadingLiveData.postValue(true)
        viewModelScope.launch {
            authInteractor.signInWithGoogle(idToken).let { result ->
                loadingLiveData.postValue(false)
                when (result) {
                    is Result.Success -> {
                        requestStatusLiveData.postValue(RequestStatus.Success)
                    }
                    is Result.Error -> {
                        requestStatusLiveData.postValue(
                            RequestStatus.Error(
                                result.message ?: "Google sign in failed"
                            )
                        )
                    }
                }
            }
        }
    }
}
