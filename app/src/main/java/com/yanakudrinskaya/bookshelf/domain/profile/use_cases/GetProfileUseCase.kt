package com.yanakudrinskaya.bookshelf.domain.profile.use_cases

import com.yanakudrinskaya.bookshelf.domain.auth.models.User
import com.yanakudrinskaya.bookshelf.domain.profile.api.UserProfileRepository
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(
    private val profileRepository: UserProfileRepository
) {
    fun getLocalUserProfileStream(): Flow<Result<User>> {
        return profileRepository.getLocalUserProfileStream()
    }
}