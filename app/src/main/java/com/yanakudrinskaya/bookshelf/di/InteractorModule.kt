package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.auth.domain.AuthInteractor
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

    factory <AuthInteractor> {
        AuthInteractorImpl(
            authRepository = get(),
            userProfileRepository = get())
    }

    factory<AvatarInteractor> {
        AvatarInteractorImpl(
            avatarRepository = get(),
            fileManager = get())
    }

    factory<FileManagerInteractor> {
        FileManagerInteractorImpl(fileManager = get())
    }

    factory<OnBoardingContentUseCase> {
        OnBoardingContentUseCase(repository = get())
    }

    single<ResourcesProviderUseCase> {
        ResourcesProviderUseCase(repository = get())
    }

    factory<BookshelfInteractor> {
        BookshelfInteractorImpl(bookshelfRepository = get())
    }

    single {
        SplashUseCase(repository = get())
    }
}