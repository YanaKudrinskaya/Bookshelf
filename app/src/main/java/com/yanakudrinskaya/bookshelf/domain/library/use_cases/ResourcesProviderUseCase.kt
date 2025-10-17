package com.yanakudrinskaya.bookshelf.domain.library.use_cases

import com.yanakudrinskaya.bookshelf.domain.library.ResourcesProviderRepository

class ResourcesProviderUseCase(
    private val repository: ResourcesProviderRepository
) {
    fun getString(resId: Int): String {
        return repository.getString(resId)
    }
}