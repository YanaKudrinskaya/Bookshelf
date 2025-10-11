package com.yanakudrinskaya.bookshelf

import android.app.Application
import com.yanakudrinskaya.bookshelf.di.dataModule
import com.yanakudrinskaya.bookshelf.di.interactorModule
import com.yanakudrinskaya.bookshelf.di.repositoryModule
import com.yanakudrinskaya.bookshelf.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(org.koin.core.logger.Level.DEBUG)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }
}