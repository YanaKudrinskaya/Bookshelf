package com.yanakudrinskaya.bookshelf.library.data

import android.content.Context
import com.yanakudrinskaya.bookshelf.library.domain.ResourcesProviderRepository

class ResourcesProviderRepositoryImpl(
    private val context: Context
) : ResourcesProviderRepository {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}