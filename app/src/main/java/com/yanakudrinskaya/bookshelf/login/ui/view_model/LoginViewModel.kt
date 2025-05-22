package com.yanakudrinskaya.bookshelf.login.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.Result
import com.yanakudrinskaya.bookshelf.login.domain.LoginScreenStateInteractor
import com.yanakudrinskaya.bookshelf.login.domain.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.login.ui.models.EditStatus
import com.yanakudrinskaya.bookshelf.login.ui.models.LoginScreenState
import com.yanakudrinskaya.bookshelf.login.ui.models.RequestStatus
import com.yanakudrinskaya.bookshelf.login.ui.models.ScreenState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userProfileInteractor: UserProfileInteractor,
    private val loginScreenStateInteractor: LoginScreenStateInteractor,
) : ViewModel() {

//    companion object {
//        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val userProfileInteractor = Creator.provideUserProfileInteractor()
//                val loginScreenStateInteractor = Creator.provideLoginScreenStateInteractor()
//                LoginViewModel(userProfileInteractor, loginScreenStateInteractor)
//            }
//        }
//    }

    private val screenStateLiveData = MutableLiveData<LoginScreenState>()
    fun getPlayStatusLiveData(): LiveData<LoginScreenState> = screenStateLiveData

    private val requestStatusLiveData = MutableLiveData<RequestStatus>()
    fun getRequestStatusLiveData(): LiveData<RequestStatus> = requestStatusLiveData

    init {
        setupContent(LoginScreenState(ScreenState.LOGIN))
    }

    fun changeContent() {
        when (getPlayStatusLiveData().value!!.screenState) {
            ScreenState.LOGIN -> setupContent(LoginScreenState(ScreenState.REGISTER))
            ScreenState.REGISTER -> setupContent(LoginScreenState(ScreenState.LOGIN))
        }
    }

    private fun setupContent(viewState: LoginScreenState) {
        when (viewState.screenState) {
            ScreenState.LOGIN -> {
                screenStateLiveData.postValue(
                    LoginScreenState(
                        viewState.screenState,
                        loginScreenStateInteractor.getLoginContent()
                    )
                )
            }

            ScreenState.REGISTER -> {
                screenStateLiveData.postValue(
                    LoginScreenState(
                        viewState.screenState,
                        loginScreenStateInteractor.getRegisterContent()
                    )
                )
            }
        }
    }

    fun processRequest(name: String, email: String, password: String, confPass: String) {
        when (getPlayStatusLiveData().value!!.screenState) {
            ScreenState.LOGIN -> login(email, password)
            ScreenState.REGISTER -> register(name, email, password, confPass)
        }
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
