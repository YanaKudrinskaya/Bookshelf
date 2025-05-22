package com.yanakudrinskaya.bookshelf.settings.domain.impl

import android.net.Uri
import android.util.Log
import com.yanakudrinskaya.bookshelf.settings.data.dto.AvatarDto
import com.yanakudrinskaya.bookshelf.settings.domain.AvatarInteractor
import com.yanakudrinskaya.bookshelf.settings.domain.AvatarManagerRepository
import com.yanakudrinskaya.bookshelf.settings.domain.FileManager
import java.io.File

class AvatarInteractorImpl(
    private val avatarRepository: AvatarManagerRepository,
    private val fileManager: FileManager
) : AvatarInteractor {

    override fun saveAvatarFromUri(userId: String, uri: Uri): Boolean {
        val tempFile = fileManager.copyUriToTempFile(uri)
        return tempFile?.let { avatarRepository.saveAvatar(userId, it) } ?: false
    }

    override fun saveAvatarFromCamera(userId: String, filePath: String): Boolean {
        return avatarRepository.saveAvatar(userId, File(filePath))
    }

    override fun loadAvatar(userId: String): AvatarDto? {
        return avatarRepository.loadAvatar(userId)
    }

    override fun deleteAvatar(userId: String) {
        avatarRepository.deleteAvatar(userId)
    }

    override fun getPlaceholder(): Int = avatarRepository.getPlaceholderResId()


}