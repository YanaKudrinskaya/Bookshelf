package com.yanakudrinskaya.bookshelf.data.library

import android.content.Context
import com.yanakudrinskaya.bookshelf.domain.library.ResourcesProviderRepository

class ResourcesProviderRepositoryImpl(
    private val context: Context
) : ResourcesProviderRepository {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}