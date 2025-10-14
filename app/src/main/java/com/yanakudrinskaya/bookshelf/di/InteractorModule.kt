package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.auth.domain.api.AuthInteractor
import com.yanakudrinskaya.bookshelf.on_boarding.domain.OnBoardingContentUseCase
import com.yanakudrinskaya.bookshelf.auth.domain.impl.AuthInteractorImpl
import com.yanakudrinskaya.bookshelf.library.domain.BookshelfInteractor
import com.yanakudrinskaya.bookshelf.library.domain.impl.BookshelfInteractorImpl
import com.yanakudrinskaya.bookshelf.library.domain.use_cases.ResourcesProviderUseCase
import com.yanakudrinskaya.bookshelf.profile.domain.AvatarInteractor
import com.yanakudrinskaya.bookshelf.profile.domain.FileManagerInteractor
import com.yanakudrinskaya.bookshelf.profile.domain.impl.AvatarInteractorImpl
import com.yanakudrinskaya.bookshelf.profile.domain.impl.FileManagerInteractorImpl
import com.yanakudrinskaya.bookshelf.splash.domain.SplashUseCase
import org.koin.dsl.module

val interactorModule = module {

    factory<AuthInteractor> {
        AuthInteractorImpl(get(), get())
    }

    factory<AvatarInteractor> {
        AvatarInteractorImpl(get(), get())
    }

    factory<FileManagerInteractor> {
        FileManagerInteractorImpl(get())
    }

    factory<OnBoardingContentUseCase> {
        OnBoardingContentUseCase(get())
    }

    single<ResourcesProviderUseCase> {
        ResourcesProviderUseCase(get())
    }

    factory<BookshelfInteractor> {
        BookshelfInteractorImpl(get())
    }

    single {
        SplashUseCase(get())
    }
}