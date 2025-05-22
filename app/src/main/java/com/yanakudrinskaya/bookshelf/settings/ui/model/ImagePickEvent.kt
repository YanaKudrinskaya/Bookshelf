package com.yanakudrinskaya.bookshelf.settings.ui.model

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest

sealed class ImagePickEvent {
    data class FromGallery(val request: PickVisualMediaRequest) : ImagePickEvent()
    data class FromCamera(val uri: Uri) : ImagePickEvent()
    data class Error(val message: String) : ImagePickEvent()
    object Default : ImagePickEvent()
}