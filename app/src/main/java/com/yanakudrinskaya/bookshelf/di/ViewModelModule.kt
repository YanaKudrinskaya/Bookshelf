package com.yanakudrinskaya.bookshelf.di

import com.yanakudrinskaya.bookshelf.boarding.ui.view_model.OnBoardingViewModel
import com.yanakudrinskaya.bookshelf.login.ui.view_model.LoginViewModel
import com.yanakudrinskaya.bookshelf.main.ui.view_model.MainViewModel
import com.yanakudrinskaya.bookshelf.settings.ui.view_model.SettingsViewModel
import com.yanakudrinskaya.bookshelf.splash.ui.view_model.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        LoginViewModel(get(), get())
    }

    viewModel {
        SplashViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get(), get())
    }

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        OnBoardingViewModel(get())
    }

}