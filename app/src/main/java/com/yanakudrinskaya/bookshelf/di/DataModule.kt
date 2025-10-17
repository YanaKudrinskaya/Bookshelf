package com.yanakudrinskaya.bookshelf.di

import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.yanakudrinskaya.bookshelf.data.auth.network.AuthProvider
import com.yanakudrinskaya.bookshelf.utils.NetworkMonitor
import com.yanakudrinskaya.bookshelf.data.auth.mappers.UserFirestoreMapper
import com.yanakudrinskaya.bookshelf.data.auth.mappers.UserSharedPrefsMapper
import com.yanakudrinskaya.bookshelf.data.auth.network.FirebaseAuthProvider
import com.yanakudrinskaya.bookshelf.data.auth.network.FirestoreUserManager
import com.yanakudrinskaya.bookshelf.data.auth.network.GoogleProvider
import com.yanakudrinskaya.bookshelf.data.auth.network.NetworkClient
import com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.FirebaseGoogleProvider
import com.yanakudrinskaya.bookshelf.data.auth.network.yandex_auth.YandexApi
import com.yanakudrinskaya.bookshelf.data.auth.network.yandex_auth.YandexRetrofitClient
import com.yanakudrinskaya.bookshelf.data.library.firebase.converters.AuthorConverter
import com.yanakudrinskaya.bookshelf.data.library.firebase.converters.BookConverter
import com.yanakudrinskaya.bookshelf.data.library.firebase.converters.WorkConverter
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseAuthorDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseBookAuthorDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseBookDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseBookWorkDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseBookshelfDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseWorkAuthorDao
import com.yanakudrinskaya.bookshelf.data.library.firebase.dao.FirebaseWorkDao
import com.yanakudrinskaya.bookshelf.data.profile.FileManagerImpl
import com.yanakudrinskaya.bookshelf.domain.profile.api.FileManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val APP_PREFERENCES = "app_prefs"

val dataModule = module {

    single<NetworkClient> {
        YandexRetrofitClient(get(), get())
    }

    single<YandexApi> {
        Retrofit.Builder()
            .baseUrl("https://login.yandex.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YandexApi::class.java)
    }

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