package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.data.on_boarding.OnBoardingRepositoryImpl
import com.yanakudrinskaya.bookshelf.domain.on_boarding.OnBoardingRepository
import com.yanakudrinskaya.bookshelf.data.auth.AuthRepositoryImpl
import com.yanakudrinskaya.bookshelf.domain.auth.api.AuthRepository
import com.yanakudrinskaya.bookshelf.domain.profile.api.LocalUserRepository
import com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.GoogleCredentialManager
import com.yanakudrinskaya.bookshelf.data.library.ResourcesProviderRepositoryImpl
import com.yanakudrinskaya.bookshelf.data.library.FirebaseBookshelfRepositoryImpl
import com.yanakudrinskaya.bookshelf.domain.library.BookshelfRepository
import com.yanakudrinskaya.bookshelf.domain.library.ResourcesProviderRepository
import com.yanakudrinskaya.bookshelf.data.profile.AvatarManagerRepositoryImpl
import com.yanakudrinskaya.bookshelf.data.profile.FileManagerImpl
import com.yanakudrinskaya.bookshelf.data.profile.LocalUserRepositoryImpl
import com.yanakudrinskaya.bookshelf.data.profile.UserProfileRepositoryImpl
import com.yanakudrinskaya.bookshelf.domain.profile.api.AvatarManagerRepository
import com.yanakudrinskaya.bookshelf.domain.profile.api.FileManager
import com.yanakudrinskaya.bookshelf.domain.profile.api.UserProfileRepository
import com.yanakudrinskaya.bookshelf.data.splash.SplashRepositoryImpl
import com.yanakudrinskaya.bookshelf.domain.splash.SplashRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get(), get(), get(), get(), get(), get())
    }

    single<LocalUserRepository> {
        LocalUserRepositoryImpl(get(), get())
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

    single<BookshelfRepository> {
        FirebaseBookshelfRepositoryImpl(
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()
        )
    }

    single {
        GoogleCredentialManager(androidContext())
    }

    single<UserProfileRepository> {
        UserProfileRepositoryImpl(get(), get())
    }
}