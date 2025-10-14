package com.yanakudrinskaya.bookshelf.auth.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.auth.domain.api.AuthInteractor
import com.yanakudrinskaya.bookshelf.auth.ui.models.RegisterUiState
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    fun getUiState(): StateFlow<RegisterUiState> = uiState.asStateFlow()

    fun register(name: String, email: String, password: String) {
        uiState.value = RegisterUiState.Loading
        viewModelScope.launch {
            authInteractor.register(name, email, password).let { result ->
                uiState.value = when (result) {
                    is Result.Success -> RegisterUiState.Success
                    is Result.Error -> RegisterUiState.Error(
                        message = result.message ?: "Registration failed"
                    )
                }
            }
        }
    }

    fun clearError() {
        uiState.value = RegisterUiState.Idle
    }
}