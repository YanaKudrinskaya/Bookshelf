package com.yanakudrinskaya.bookshelf.auth.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.auth.domain.api.AuthInteractor
import com.yanakudrinskaya.bookshelf.auth.ui.models.LoginUiState
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    fun getUiState(): StateFlow<LoginUiState> = uiState.asStateFlow()

    fun login(email: String, password: String) {
        performAuthOperation { authInteractor.login(email, password) }
    }

    fun signInWithGoogle(idToken: String) {
        performAuthOperation { authInteractor.signInWithGoogle(idToken) }
    }

    fun clearError() {
        uiState.value = LoginUiState.Idle
    }

    private fun performAuthOperation(operation: suspend () -> Result<*>) {
        uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            when (val result = operation()) {
                is Result.Success -> {
                    uiState.value = LoginUiState.Success
                }
                is Result.Error -> {
                    uiState.value = LoginUiState.Error(
                        message = result.message ?: "Authentication failed"
                    )
                }
            }
        }
    }
}
