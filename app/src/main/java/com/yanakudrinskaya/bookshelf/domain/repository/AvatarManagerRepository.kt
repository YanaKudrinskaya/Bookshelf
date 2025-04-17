package com.yanakudrinskaya.bookshelf.domain.repository

import com.yanakudrinskaya.bookshelf.data.models.AvatarDto
import java.io.File

interface AvatarManagerRepository {
    fun saveAvatar(userId: String, sourceFile: File): Boolean
    fun loadAvatar(userId: String): AvatarDto?
    fun deleteAvatar(userId: String)
}