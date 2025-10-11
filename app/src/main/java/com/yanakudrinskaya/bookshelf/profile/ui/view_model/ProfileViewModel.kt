package com.yanakudrinskaya.bookshelf.profile.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.profile.domain.AvatarInteractor
import com.yanakudrinskaya.bookshelf.profile.ui.model.UserState
import kotlinx.coroutines.launch
import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.yanakudrinskaya.bookshelf.profile.ui.model.ImagePickEvent
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.profile.domain.FileManagerInteractor
import com.yanakudrinskaya.bookshelf.profile.ui.model.PermissionState
import android.content.pm.PackageManager
import android.util.Log
import com.yanakudrinskaya.bookshelf.auth.domain.AuthInteractor
import com.yanakudrinskaya.bookshelf.auth.domain.models.User

class ProfileViewModel(
    private val avatarInteractor: AvatarInteractor,
    private val fileManagerInteractor: FileManagerInteractor,
    private var authInteractor: AuthInteractor,
) : ViewModel() {

    private val userLiveData = MutableLiveData<UserState>()
    fun getUserLiveData(): LiveData<UserState> = userLiveData

    private val imagePickEvent = MutableLiveData<ImagePickEvent>()
    fun getImagePickEvent(): LiveData<ImagePickEvent> = imagePickEvent

    private val permissionState = MutableLiveData<PermissionState>()
    fun getPermissionState(): LiveData<PermissionState> = permissionState

    private lateinit var currentPhotoPath: String
    private val PERMISSIONS_REQUEST = 100

    private lateinit var user: User
    private var currentAvatarPath: String? = null

    init {
        loadUserProfile()
        checkPermissions()
    }

    fun logout() {
        authInteractor.logout()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            authInteractor.getCurrentUser().collect { result ->
                when (result) {
                    is Result.Error -> {
                        Log.e("ProfileViewModel", "Error loading user: ${result.message}")
                        userLiveData.value = UserState(
                            name = "",
                            email = "",
                            placeholder = avatarInteractor.getPlaceholder()
                        )
                    }

                    is Result.Success -> {
                        user = result.data
                        userLiveData.value = UserState(
                            name = user.name,
                            email = user.email,
                            placeholder = avatarInteractor.getPlaceholder()
                        )
                        loadAvatar()
                    }
                }
            }
        }

    }

    fun loadAvatar() {
        val avatar = avatarInteractor.loadAvatar(user.userId)
        if (avatar?.filePath != currentAvatarPath) {
            currentAvatarPath = avatar?.filePath
            userLiveData.value = getCurrentPlayStatus().copy(
                filePath = currentAvatarPath,
                avatarIsChange = true
            )
        }
    }

    fun deleteAvatar() {
        avatarInteractor.deleteAvatar(user.userId)
        userLiveData.value = getCurrentPlayStatus().copy(filePath = null, avatarIsChange = true)
    }

    fun checkPermissions(): Array<String> {
        return mutableListOf<String>().apply {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST) {
            val deniedPermissions = mutableListOf<String>()

            grantResults.forEachIndexed { index, result ->
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[index])
                }
            }

            permissionState.value = if (deniedPermissions.isEmpty()) {
                PermissionState.Granted
            } else {
                PermissionState.Denied(deniedPermissions)
            }
        }
    }

    fun processSelectedImage(uri: Uri?, fromCamera: Boolean) {
        viewModelScope.launch {
            val success = if (fromCamera) {
                avatarInteractor.saveAvatarFromCamera(user.userId, currentPhotoPath)
            } else {
                uri?.let { avatarInteractor.saveAvatarFromUri(user.userId, it) } ?: false
            }

            if (success) {
                loadAvatar()
            } else {
                imagePickEvent.value = ImagePickEvent.Error(
                    "Ошибка сохранения"
                )
            }
        }
    }

    fun createImageFile(): Result<String> {
        return fileManagerInteractor.createTempImageFile()
    }

    fun onGallerySelected() {
        imagePickEvent.value = ImagePickEvent.FromGallery(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    fun onCameraSelected() {
        when (val result = createImageFile()) {
            is Result.Success -> {
                currentPhotoPath = result.data
                when (val uriResult = fileManagerInteractor.getUriForFile(result.data)) {
                    is Result.Success -> {
                        imagePickEvent.value = ImagePickEvent.FromCamera(uriResult.data)
                    }

                    is Result.Error -> {
                        imagePickEvent.value = ImagePickEvent.Error("")
                    }
                }
            }

            is Result.Error -> imagePickEvent.value = ImagePickEvent.Error("")
        }
    }

    fun onDefaultSelected() {
        imagePickEvent.value = ImagePickEvent.Default
    }

    private fun getCurrentPlayStatus(): UserState {
        return userLiveData.value ?: UserState(name = "", email = "")
    }

    fun changeName(name: String) {
        if (name.isNotEmpty()) {
            viewModelScope.launch {
                authInteractor.updateUserName(name).let { result ->
                    when (result) {
                        is Result.Success -> {
                            Log.d("Myregister", "Изменение имени пользователя прошло успешно")
                            user = result.data
                            userLiveData.postValue(
                                getCurrentPlayStatus().copy(
                                    name = user.name,
                                    avatarIsChange = false
                                )
                            )
                        }

                        is Result.Error -> {
                            Log.d("Myregister", "Ошибка")
                            imagePickEvent.postValue(ImagePickEvent.Error(""))
                        }
                    }
                }
            }
        }
    }
}