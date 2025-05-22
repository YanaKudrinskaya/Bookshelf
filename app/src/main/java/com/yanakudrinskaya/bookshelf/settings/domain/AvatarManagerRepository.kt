package com.yanakudrinskaya.bookshelf.settings.domain

import com.google.rpc.context.AttributeContext.Resource
import com.yanakudrinskaya.bookshelf.settings.data.dto.AvatarDto
import java.io.File

interface AvatarManagerRepository {
    fun saveAvatar(userId: String, sourceFile: File): Boolean
    fun loadAvatar(userId: String): AvatarDto?
    fun deleteAvatar(userId: String)
    fun getPlaceholderResId() : Int
}