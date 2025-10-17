package com.yanakudrinskaya.bookshelf.data.splash

import android.content.SharedPreferences
import androidx.core.content.edit
import com.yanakudrinskaya.bookshelf.domain.splash.SplashRepository

class SplashRepositoryImpl (
    private val sharedPreferences: SharedPreferences,
) : SplashRepository {

    override fun isFirstLaunch(): Boolean {
        return if (sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)) {
            sharedPreferences.edit { putBoolean(IS_FIRST_LAUNCH, false) }
            true
        } else false
    }

    companion object {
        private const val IS_FIRST_LAUNCH = "is_first_launch"
    }
}