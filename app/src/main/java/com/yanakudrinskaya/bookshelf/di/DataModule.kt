package com.yanakudrinskaya.bookshelf.di

import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.yanakudrinskaya.bookshelf.auth.data.network.AuthProvider
import com.yanakudrinskaya.bookshelf.utils.NetworkMonitor
import com.yanakudrinskaya.bookshelf.auth.data.mappers.UserFirestoreMapper
import com.yanakudrinskaya.bookshelf.auth.data.mappers.UserSharedPrefsMapper
import com.yanakudrinskaya.bookshelf.auth.data.network.FirebaseAuthProvider
import com.yanakudrinskaya.bookshelf.auth.data.network.FirestoreUserManager
import com.yanakudrinskaya.bookshelf.auth.data.network.GoogleProvider
import com.yanakudrinskaya.bookshelf.auth.data.network.google_auth.FirebaseGoogleProvider
import com.yanakudrinskaya.bookshelf.library.data.firebase.converters.AuthorConverter
import com.yanakudrinskaya.bookshelf.library.data.firebase.converters.BookConverter
import com.yanakudrinskaya.bookshelf.library.data.firebase.converters.WorkConverter
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseAuthorDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseBookAuthorDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseBookDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseBookWorkDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseBookshelfDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseWorkAuthorDao
import com.yanakudrinskaya.bookshelf.library.data.firebase.dao.FirebaseWorkDao
import com.yanakudrinskaya.bookshelf.profile.data.FileManagerImpl
import com.yanakudrinskaya.bookshelf.profile.domain.api.FileManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val APP_PREFERENCES = "app_prefs"

val dataModule = module {

    single {
        FirebaseAuth.getInstance()
    }

    single {
        FirebaseFirestore.getInstance()
    }

    single<GoogleProvider> {
        FirebaseGoogleProvider(get())
    }

    single { UserSharedPrefsMapper(gson = get()) }
    single { UserFirestoreMapper() }

    single { FirebaseBookshelfDao(get()) }
    single { FirebaseAuthorDao(get()) }
    single { FirebaseBookDao(get()) }
    single { FirebaseWorkDao(get()) }
    single { FirebaseBookAuthorDao(get()) }
    single { FirebaseBookWorkDao(get()) }
    single { AuthorConverter() }
    single { BookConverter(get(), get()) }
    single { WorkConverter(get()) }
    single { FirebaseWorkAuthorDao(get()) }

    single<ConnectivityManager> {
        androidContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    single {
        androidContext()
            .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    single {
        NetworkMonitor(
            context = androidContext(),
            connectivityManager = get()
        )
    }

    single {
        FirestoreUserManager(get(), get())
    }

    single<AuthProvider> {
        FirebaseAuthProvider(get(), get())
    }

    factory { Gson() }

    single<FileManager> {
        FileManagerImpl(androidContext())
    }
}