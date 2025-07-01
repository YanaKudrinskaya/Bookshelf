package com.yanakudrinskaya.bookshelf.di

import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.yanakudrinskaya.bookshelf.auth.data.APP_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }

    single<FirebaseFirestore> {
        FirebaseFirestore.getInstance()
    }

    single<ConnectivityManager> {
        androidContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    single {
        androidContext()
            .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

}