package com.yanakudrinskaya.bookshelf.ui.profile.model

sealed class PermissionState {
    object Granted : PermissionState()
    data class Denied(val permissions: List<String>) : PermissionState()
    data class Error(val message: String) : PermissionState()
}