package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.boarding.data.OnBoardingRepositoryImpl
import com.yanakudrinskaya.bookshelf.boarding.domain.OnBoardingRepository
import com.yanakudrinskaya.bookshelf.login.data.AuthRepositoryImpl
import com.yanakudrinskaya.bookshelf.login.data.LoginScreenStateRepositoryImpl
import com.yanakudrinskaya.bookshelf.login.data.NetworkMonitorRepositoryImpl
import com.yanakudrinskaya.bookshelf.login.data.UserProfileRepositoryImpl
import com.yanakudrinskaya.bookshelf.login.domain.AuthRepository
import com.yanakudrinskaya.bookshelf.login.domain.LoginScreenStateRepository
import com.yanakudrinskaya.bookshelf.login.domain.NetworkMonitorRepository
import com.yanakudrinskaya.bookshelf.login.domain.UserProfileRepository
import com.yanakudrinskaya.bookshelf.settings.data.AvatarManagerRepositoryImpl
import com.yanakudrinskaya.bookshelf.settings.data.FileManagerImpl
import com.yanakudrinskaya.bookshelf.settings.domain.AvatarManagerRepository
import com.yanakudrinskaya.bookshelf.settings.domain.FileManager
import com.yanakudrinskaya.bookshelf.splash.data.SplashRepositoryImpl
import com.yanakudrinskaya.bookshelf.splash.domain.SplashRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    single<LoginScreenStateRepository> {
        LoginScreenStateRepositoryImpl(androidContext())
    }

    single<NetworkMonitorRepository> {
        NetworkMonitorRepositoryImpl(androidContext(), get())
    }

    single<UserProfileRepository> {
        UserProfileRepositoryImpl(get(), get())
    }

    single<AvatarManagerRepository> {
        AvatarManagerRepositoryImpl(androidContext())
    }

    single<FileManager> {
        FileManagerImpl(androidContext())
    }

    single<SplashRepository> {
        SplashRepositoryImpl(get())
    }

    single<OnBoardingRepository> {
        OnBoardingRepositoryImpl(androidContext())
    }

}