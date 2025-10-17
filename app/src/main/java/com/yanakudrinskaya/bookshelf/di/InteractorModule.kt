package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.domain.auth.api.AuthInteractor
import com.yanakudrinskaya.bookshelf.domain.on_boarding.OnBoardingContentUseCase
import com.yanakudrinskaya.bookshelf.domain.auth.impl.AuthInteractorImpl
import com.yanakudrinskaya.bookshelf.domain.library.BookshelfInteractor
import com.yanakudrinskaya.bookshelf.domain.library.impl.BookshelfInteractorImpl
import com.yanakudrinskaya.bookshelf.domain.library.use_cases.ResourcesProviderUseCase
import com.yanakudrinskaya.bookshelf.domain.profile.api.AvatarInteractor
import com.yanakudrinskaya.bookshelf.domain.profile.impl.AvatarInteractorImpl
import com.yanakudrinskaya.bookshelf.domain.profile.use_cases.GetProfileUseCase
import com.yanakudrinskaya.bookshelf.domain.profile.use_cases.LogoutUseCase
import com.yanakudrinskaya.bookshelf.domain.profile.use_cases.UpdateUserNameUseCase
import com.yanakudrinskaya.bookshelf.domain.splash.SplashUseCase
import org.koin.dsl.module

val interactorModule = module {

    factory<AuthInteractor> {
        AuthInteractorImpl(get())
    }

    factory<AvatarInteractor> {
        AvatarInteractorImpl(get(), get())
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

    single {
        LogoutUseCase(get())
    }

    single {
        UpdateUserNameUseCase(get())
    }

    single {
        GetProfileUseCase(get())
    }
}