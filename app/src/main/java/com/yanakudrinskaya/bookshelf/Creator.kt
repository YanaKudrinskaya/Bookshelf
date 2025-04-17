package com.yanakudrinskaya.bookshelf

import android.app.Application
import com.yanakudrinskaya.bookshelf.data.repository.AuthRepositoryImpl
import com.yanakudrinskaya.bookshelf.data.repository.AvatarManagerRepositoryImpl
import com.yanakudrinskaya.bookshelf.data.repository.NetworkMonitorRepositoryImpl
import com.yanakudrinskaya.bookshelf.data.repository.UserProfileRepositoryImpl
import com.yanakudrinskaya.bookshelf.domain.impl.AvatarUseCase
import com.yanakudrinskaya.bookshelf.domain.impl.UserProfileInteractorImpl
import com.yanakudrinskaya.bookshelf.domain.interactors.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.domain.repository.AuthRepository
import com.yanakudrinskaya.bookshelf.domain.repository.AvatarManagerRepository
import com.yanakudrinskaya.bookshelf.domain.repository.NetworkMonitorRepository
import com.yanakudrinskaya.bookshelf.domain.repository.UserProfileRepository

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    private fun getUserProfileRepository(): UserProfileRepository {
        return UserProfileRepositoryImpl(application)
    }

    private fun getNetworkMonitorRepository(): NetworkMonitorRepository {
        return NetworkMonitorRepositoryImpl(application)
    }

    private fun getAvatarManagerRepository(): AvatarManagerRepository {
        return AvatarManagerRepositoryImpl(application)
    }

    fun provideUserProfileInteractor(): UserProfileInteractor {
        return UserProfileInteractorImpl(getAuthRepository(), getUserProfileRepository(), getNetworkMonitorRepository())
    }

    fun provideAvatarUseCase() : AvatarUseCase {
        return AvatarUseCase(getAvatarManagerRepository(), application)
    }
}