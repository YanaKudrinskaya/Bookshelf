package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.on_boarding.data.OnBoardingRepositoryImpl
import com.yanakudrinskaya.bookshelf.on_boarding.domain.OnBoardingRepository
import com.yanakudrinskaya.bookshelf.auth.data.AuthRepositoryImpl
import com.yanakudrinskaya.bookshelf.auth.domain.api.AuthRepository
import com.yanakudrinskaya.bookshelf.profile.domain.api.LocalUserRepository
import com.yanakudrinskaya.bookshelf.auth.data.utils.GoogleCredentialManager
import com.yanakudrinskaya.bookshelf.library.data.ResourcesProviderRepositoryImpl
import com.yanakudrinskaya.bookshelf.library.data.FirebaseBookshelfRepositoryImpl
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfRepository
import com.yanakudrinskaya.bookshelf.library.domain.ResourcesProviderRepository
import com.yanakudrinskaya.bookshelf.profile.data.AvatarManagerRepositoryImpl
import com.yanakudrinskaya.bookshelf.profile.data.FileManagerImpl
import com.yanakudrinskaya.bookshelf.profile.data.LocalUserRepositoryImpl
import com.yanakudrinskaya.bookshelf.profile.data.UserProfileRepositoryImpl
import com.yanakudrinskaya.bookshelf.profile.domain.api.AvatarManagerRepository
import com.yanakudrinskaya.bookshelf.profile.domain.api.FileManager
import com.yanakudrinskaya.bookshelf.profile.domain.api.UserProfileRepository
import com.yanakudrinskaya.bookshelf.splash.data.SplashRepositoryImpl
import com.yanakudrinskaya.bookshelf.splash.domain.SplashRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get(), get(), get(), get())
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