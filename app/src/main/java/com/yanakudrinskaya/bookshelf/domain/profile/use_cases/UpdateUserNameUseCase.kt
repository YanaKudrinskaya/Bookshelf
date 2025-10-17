package com.yanakudrinskaya.bookshelf.domain.profile.use_cases

import com.yanakudrinskaya.bookshelf.domain.profile.api.UserProfileRepository

class UpdateUserNameUseCase(
    private val profileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userId: String, newName: String) {
        profileRepository.updateUserNameLocally(userId, newName)
        profileRepository.updateUserName(newName)
    }
}