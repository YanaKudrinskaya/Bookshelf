package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.splash.domain.impl.OnBoardingContentUseCase
import com.yanakudrinskaya.bookshelf.auth.domain.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.auth.domain.impl.UserProfileInteractorImpl
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfInteractor
import com.yanakudrinskaya.bookshelf.library.domain.impl.BookshelfInteractorImpl
import com.yanakudrinskaya.bookshelf.library.domain.use_cases.ResourcesProviderUseCase
import com.yanakudrinskaya.bookshelf.profile.domain.AvatarInteractor
import com.yanakudrinskaya.bookshelf.profile.domain.FileManagerInteractor
import com.yanakudrinskaya.bookshelf.profile.domain.impl.AvatarInteractorImpl
import com.yanakudrinskaya.bookshelf.profile.domain.impl.FileManagerInteractorImpl
import com.yanakudrinskaya.bookshelf.splash.domain.use_cases.SplashUseCase
import org.koin.dsl.module

val interactorModule = module {

    single<UserProfileInteractor> {
        UserProfileInteractorImpl(get(), get(), get())
    }

    single<AvatarInteractor> {
        AvatarInteractorImpl(get(), get())
    }

    single<SplashUseCase> {
        SplashUseCase(get())
    }

    single<FileManagerInteractor> {
        FileManagerInteractorImpl(get())
    }

    single<OnBoardingContentUseCase> {
        OnBoardingContentUseCase(get())
    }

    single<ResourcesProviderUseCase> {
        ResourcesProviderUseCase(get())
    }

    single<BookshelfInteractor> {
        BookshelfInteractorImpl(get())
    }

}