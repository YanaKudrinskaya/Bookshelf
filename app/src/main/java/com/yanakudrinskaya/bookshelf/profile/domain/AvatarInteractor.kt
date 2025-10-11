package com.yanakudrinskaya.bookshelf.profile.domain

import android.net.Uri
import com.yanakudrinskaya.bookshelf.profile.data.dto.AvatarDto

interface AvatarInteractor {
    fun saveAvatarFromUri(userId: String, uri: Uri): Boolean
    fun saveAvatarFromCamera(userId: String, filePath: String): Boolean
    fun loadAvatar(userId: String): AvatarDto?
    fun deleteAvatar(userId: String)
    fun getPlaceholder(): Int
}