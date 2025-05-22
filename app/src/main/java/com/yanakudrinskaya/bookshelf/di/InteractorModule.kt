package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.boarding.domain.impl.OnBoardingContentUseCase
import com.yanakudrinskaya.bookshelf.login.domain.LoginScreenStateInteractor
import com.yanakudrinskaya.bookshelf.login.domain.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.login.domain.impl.LoginScreenStateInteractorImpl
import com.yanakudrinskaya.bookshelf.login.domain.impl.UserProfileInteractorImpl
import com.yanakudrinskaya.bookshelf.settings.domain.AvatarInteractor
import com.yanakudrinskaya.bookshelf.settings.domain.FileManagerInteractor
import com.yanakudrinskaya.bookshelf.settings.domain.impl.AvatarInteractorImpl
import com.yanakudrinskaya.bookshelf.settings.domain.impl.FileManagerInteractorImpl
import com.yanakudrinskaya.bookshelf.splash.domain.use_cases.SplashUseCase
import org.koin.dsl.module

val interactorModule = module {

    single<UserProfileInteractor> {
        UserProfileInteractorImpl(get(), get(), get())
    }

    single<LoginScreenStateInteractor> {
        LoginScreenStateInteractorImpl(get())
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

}