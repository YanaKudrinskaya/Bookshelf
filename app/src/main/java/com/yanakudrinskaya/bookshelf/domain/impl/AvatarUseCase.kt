package com.yanakudrinskaya.bookshelf.domain.impl

import android.content.Context
import android.net.Uri
import android.util.Log
import com.yanakudrinskaya.bookshelf.data.models.AvatarDto
import com.yanakudrinskaya.bookshelf.domain.repository.AvatarManagerRepository
import java.io.File

class AvatarUseCase(
    private val avatarRepository: AvatarManagerRepository,
    private val context: Context
) {
    fun saveAvatarFromUri(userId: String, uri: Uri): Boolean {
        return try {
            val tempFile = createTempFile(context)
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            avatarRepository.saveAvatar(userId, tempFile)
        } catch (e: Exception) {
            false
        }
    }

    fun saveAvatarFromCamera(userId: String, filePath: String): Boolean {
        return avatarRepository.saveAvatar(userId, File(filePath))
    }

    fun loadAvatar(userId: String): AvatarDto? {
        Log.d("Myregister", "loadAvatar: $userId")
        return avatarRepository.loadAvatar(userId)
    }

    private fun createTempFile(context: Context): File {
        return File.createTempFile(
            "temp_avatar_",
            ".jpg",
            context.cacheDir
        )
    }

    fun deleteAvatar(userId: String) {
        avatarRepository.deleteAvatar(userId)
    }
}