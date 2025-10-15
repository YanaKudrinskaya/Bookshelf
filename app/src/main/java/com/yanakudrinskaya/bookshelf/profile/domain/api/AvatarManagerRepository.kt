package com.yanakudrinskaya.bookshelf.profile.domain.api

import com.yanakudrinskaya.bookshelf.profile.data.dto.AvatarDto
import java.io.File

interface AvatarManagerRepository {
    fun saveAvatar(userId: String, sourceFile: File): Boolean
    fun loadAvatar(userId: String): AvatarDto?
    fun deleteAvatar(userId: String)
    fun getPlaceholderResId() : Int
}