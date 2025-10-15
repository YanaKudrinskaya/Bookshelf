package com.yanakudrinskaya.bookshelf.splash.data

import android.content.SharedPreferences
import com.yanakudrinskaya.bookshelf.splash.domain.SplashRepository
import androidx.core.content.edit

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