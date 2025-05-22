package com.yanakudrinskaya.bookshelf.splash.data

import android.content.SharedPreferences
import com.yanakudrinskaya.bookshelf.login.data.IS_FIRST_LAUNCH
import com.yanakudrinskaya.bookshelf.splash.domain.SplashRepository

class SplashRepositoryImpl (
    private val sharedPreferences: SharedPreferences,
) : SplashRepository {

    override fun isFirstLaunch(): Boolean {
        return if (sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)) {
            sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, false).apply()
            true
        } else false
    }

}