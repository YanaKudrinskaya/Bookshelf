package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.ui.add_book.view_model.AddBookViewModel
import com.yanakudrinskaya.bookshelf.ui.library.view_model.LibraryViewModel
import com.yanakudrinskaya.bookshelf.ui.wish.view_model.WishViewModel
import com.yanakudrinskaya.bookshelf.ui.on_boarding.view_model.OnBoardingViewModel
import com.yanakudrinskaya.bookshelf.ui.auth.view_model.LoginViewModel
import com.yanakudrinskaya.bookshelf.ui.auth.view_model.RegisterViewModel
import com.yanakudrinskaya.bookshelf.ui.profile.view_model.ProfileViewModel
import com.yanakudrinskaya.bookshelf.ui.root.view_model.RootViewModel
import com.yanakudrinskaya.bookshelf.ui.splash.view_model.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        RootViewModel()
    }

    viewModel {
        SplashViewModel(
            authInteractor = get(),
            splashUseCase = get()
        )
    }

    viewModel {
        OnBoardingViewModel(onBoardingUseCase = get())
    }

    viewModel {
        LoginViewModel(authInteractor = get())
    }

    viewModel {
        RegisterViewModel(authInteractor = get())
    }

    viewModel {
        LibraryViewModel(bookshelfInteractor = get())
    }

    viewModel {
        WishViewModel()
    }

    viewModel {
        ProfileViewModel(get(), get(), get(), get(), get())
    }

    viewModel {
        AddBookViewModel(bookshelfInteractor = get())
    }
}