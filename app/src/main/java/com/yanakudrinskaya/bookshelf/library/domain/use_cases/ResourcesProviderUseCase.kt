package com.yanakudrinskaya.bookshelf.library.domain.use_cases

import com.yanakudrinskaya.bookshelf.library.domain.ResourcesProviderRepository

class ResourcesProviderUseCase(
    private val repository: ResourcesProviderRepository
) {
    fun getString(resId: Int): String {
        return repository.getString(resId)
    }
}