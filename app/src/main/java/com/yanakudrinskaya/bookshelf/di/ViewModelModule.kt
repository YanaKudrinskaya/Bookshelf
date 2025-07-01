package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.add_book.ui.view_model.AddBookViewModel
import com.yanakudrinskaya.bookshelf.library.ui.view_model.LibraryViewModel
import com.yanakudrinskaya.bookshelf.wish.ui.view_model.WishViewModel
import com.yanakudrinskaya.bookshelf.on_boarding.ui.view_model.OnBoardingViewModel
import com.yanakudrinskaya.bookshelf.auth.ui.view_model.LoginViewModel
import com.yanakudrinskaya.bookshelf.auth.ui.view_model.RegisterViewModel
import com.yanakudrinskaya.bookshelf.profile.ui.view_model.ProfileViewModel
import com.yanakudrinskaya.bookshelf.root.ui.view_model.RootViewModel
import com.yanakudrinskaya.bookshelf.splash.ui.view_model.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        RootViewModel()
    }

    viewModel {
        SplashViewModel(get(), get())
    }

    viewModel {
        OnBoardingViewModel(get())
    }

    viewModel {
        LoginViewModel(get())
    }

    viewModel {
        LibraryViewModel(get())
    }

    viewModel {
        WishViewModel()
    }

    viewModel {
        ProfileViewModel(get(), get(), get())
    }

    viewModel {
        AddBookViewModel(get())
    }

    viewModel {
        RegisterViewModel(get())
    }

}