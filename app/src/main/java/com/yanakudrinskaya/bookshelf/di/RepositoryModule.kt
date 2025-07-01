package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.splash.data.OnBoardingRepositoryImpl
import com.yanakudrinskaya.bookshelf.splash.domain.OnBoardingRepository
import com.yanakudrinskaya.bookshelf.auth.data.AuthRepositoryImpl
import com.yanakudrinskaya.bookshelf.auth.data.NetworkMonitorRepositoryImpl
import com.yanakudrinskaya.bookshelf.auth.data.UserProfileRepositoryImpl
import com.yanakudrinskaya.bookshelf.auth.domain.AuthRepository
import com.yanakudrinskaya.bookshelf.auth.domain.NetworkMonitorRepository
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileRepository
import com.yanakudrinskaya.bookshelf.library.data.ResourcesProviderRepositoryImpl
import com.yanakudrinskaya.bookshelf.library.data.BookshelfLocalRepositoryImpl
import com.yanakudrinskaya.bookshelf.library.data.BookshelfRepositoryImpl
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfLocalRepository
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfRepository
import com.yanakudrinskaya.bookshelf.library.domain.ResourcesProviderRepository
import com.yanakudrinskaya.bookshelf.profile.data.AvatarManagerRepositoryImpl
import com.yanakudrinskaya.bookshelf.profile.data.FileManagerImpl
import com.yanakudrinskaya.bookshelf.profile.domain.AvatarManagerRepository
import com.yanakudrinskaya.bookshelf.profile.domain.FileManager
import com.yanakudrinskaya.bookshelf.splash.data.SplashRepositoryImpl
import com.yanakudrinskaya.bookshelf.splash.domain.SplashRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
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

    single<ResourcesProviderRepository> {
        ResourcesProviderRepositoryImpl(androidContext())
    }

    single<BookshelfLocalRepository> {
        BookshelfLocalRepositoryImpl(get(), get())
    }

    single<BookshelfRepository> {
        BookshelfRepositoryImpl(get())
    }

}