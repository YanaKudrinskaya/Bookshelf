package com.yanakudrinskaya.bookshelf.profile.domain.impl

import android.net.Uri
import com.yanakudrinskaya.bookshelf.profile.data.dto.AvatarDto
import com.yanakudrinskaya.bookshelf.profile.domain.api.AvatarInteractor
import com.yanakudrinskaya.bookshelf.profile.domain.api.AvatarManagerRepository
import com.yanakudrinskaya.bookshelf.profile.domain.api.FileManager
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