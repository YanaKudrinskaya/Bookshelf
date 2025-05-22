package com.yanakudrinskaya.bookshelf.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.bookshelf.login.domain.models.UserCurrent
import com.yanakudrinskaya.bookshelf.settings.domain.AvatarInteractor
import com.yanakudrinskaya.bookshelf.settings.ui.model.UserState
import kotlinx.coroutines.launch
import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.yanakudrinskaya.bookshelf.settings.ui.model.ImagePickEvent
import com.yanakudrinskaya.bookshelf.Result
import com.yanakudrinskaya.bookshelf.settings.domain.FileManagerInteractor
import com.yanakudrinskaya.bookshelf.settings.ui.model.PermissionState
import android.content.pm.PackageManager
import android.util.Log
import com.yanakudrinskaya.bookshelf.login.domain.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.settings.ui.model.Event

class SettingsViewModel (
    private val avatarInteractor: AvatarInteractor,
    private val fileManagerInteractor: FileManagerInteractor,
    private var userProfileInteractor: UserProfileInteractor,
) : ViewModel() {

    private val userLiveData = MutableLiveData<UserState>()
    fun getUserLiveData() : LiveData<UserState> = userLiveData

    private val imagePickEvent = MutableLiveData<ImagePickEvent>()
    fun getImagePickEvent(): LiveData<ImagePickEvent> = imagePickEvent

    private val permissionState = MutableLiveData<PermissionState>()
    fun getPermissionState(): LiveData<PermissionState> = permissionState

    private val logoutEvent = MutableLiveData<Event<Unit>>()
    fun getLogoutEvent(): LiveData<Event<Unit>> = logoutEvent

    private lateinit var currentPhotoPath: String
    private val PERMISSIONS_REQUEST = 100

    fun triggerLogout() {
        userProfileInteractor.logout()
        logoutEvent.value = Event(Unit)
    }

    fun loadUserProfile() {
        userLiveData.value = UserState(
            name = UserCurrent.name,
            email = UserCurrent.email,
            placeholder = avatarInteractor.getPlaceholder(),
        )
    }

    fun loadAvatar() {
        viewModelScope.launch {
            avatarInteractor.loadAvatar(UserCurrent.id)?.let { avatar ->
                userLiveData.value = getCurrentPlayStatus().copy(filePath = avatar.filePath)
            } ?: run {
                userLiveData.value = getCurrentPlayStatus().copy(filePath = null)
            }
        }
    }

    fun deleteAvatar() {
        avatarInteractor.deleteAvatar(UserCurrent.id)
        userLiveData.value = getCurrentPlayStatus().copy(filePath = null)
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
                avatarInteractor.saveAvatarFromCamera(UserCurrent.id, currentPhotoPath)
            } else {
                uri?.let { avatarInteractor.saveAvatarFromUri(UserCurrent.id, it) } ?: false
            }

            if (success) {
                loadAvatar()
            } else {
                imagePickEvent.value = ImagePickEvent.Error(
                    "Ошибка сохранения")
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
                    is Result.Failure -> {
                        imagePickEvent.value = ImagePickEvent.Error(uriResult.exception.message ?: "")
                    }
                }
            }
            is Result.Failure -> imagePickEvent.value = ImagePickEvent.Error(result.exception.message ?: "")
        }
    }

    fun onDefaultSelected() {
        imagePickEvent.value = ImagePickEvent.Default
    }

    private fun getCurrentPlayStatus(): UserState {
        return userLiveData.value ?: UserState(name = "", email = "")
    }
}