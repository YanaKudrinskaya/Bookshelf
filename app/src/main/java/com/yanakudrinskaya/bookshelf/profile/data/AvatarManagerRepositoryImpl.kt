package com.yanakudrinskaya.bookshelf.profile.data

import android.content.Context
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.profile.data.dto.AvatarDto
import com.yanakudrinskaya.bookshelf.profile.domain.api.AvatarManagerRepository
import java.io.File

class AvatarManagerRepositoryImpl(
    private val context: Context
) : AvatarManagerRepository {

    private val avatarsDir = File(context.filesDir, "avatars").apply {
        if (!exists()) mkdirs()
    }

    private fun getAvatarFile(userId: String): File {
        return File(avatarsDir, "avatar_$userId.jpg")
    }

    override fun saveAvatar(userId: String, sourceFile: File): Boolean {
        return try {
            val destFile = getAvatarFile(userId)
            sourceFile.copyTo(destFile, overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun loadAvatar(userId: String): AvatarDto? {
        val file = getAvatarFile(userId)
        return if (file.exists()) AvatarDto(file.absolutePath) else null
    }

    override fun deleteAvatar(userId: String) {
        getAvatarFile(userId).delete()
    }

    override fun getPlaceholderResId(): Int = R.drawable.placeholder
}