package com.yanakudrinskaya.bookshelf.domain.profile.api

import com.yanakudrinskaya.bookshelf.data.profile.dto.AvatarDto
import java.io.File

interface AvatarManagerRepository {
    fun saveAvatar(userId: String, sourceFile: File): Boolean
    fun loadAvatar(userId: String): AvatarDto?
    fun deleteAvatar(userId: String)
    fun getPlaceholderResId() : Int
}