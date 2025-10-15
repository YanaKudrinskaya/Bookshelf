package com.yanakudrinskaya.bookshelf.profile.domain.use_cases

import com.yanakudrinskaya.bookshelf.profile.domain.api.UserProfileRepository

class UpdateUserNameUseCase(
    private val profileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userId: String, newName: String) {
        profileRepository.updateUserNameLocally(userId, newName)
        profileRepository.updateUserName(newName)
    }
}