package com.yanakudrinskaya.bookshelf.profile.domain.use_cases

import com.yanakudrinskaya.bookshelf.auth.domain.models.User
import com.yanakudrinskaya.bookshelf.profile.domain.api.UserProfileRepository
import com.yanakudrinskaya.bookshelf.utils.Result
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(
    private val profileRepository: UserProfileRepository
) {
    fun getLocalUserProfileStream(): Flow<Result<User>> {
        return profileRepository.getLocalUserProfileStream()
    }
}