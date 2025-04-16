package com.yanakudrinskaya.bookshelf.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.yanakudrinskaya.bookshelf.Creator
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.data.repository.APP_PREFERENCES
import com.yanakudrinskaya.bookshelf.data.repository.IS_FIRST_LAUNCH
import com.yanakudrinskaya.bookshelf.domain.interactors.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.domain.models.Result
import com.yanakudrinskaya.bookshelf.domain.models.UserCurrent
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var userProfileInteractor: UserProfileInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userProfileInteractor = Creator.provideUserProfileInteractor()
        checkFirstLaunch()

    }

    private fun checkFirstLaunch() {
        val prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        if (prefs.getBoolean(IS_FIRST_LAUNCH, true)) {
            prefs.edit().putBoolean(IS_FIRST_LAUNCH, false).apply()
            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
        } else {
            checkAuthAndLoadData()
        }
    }

    private fun checkAuthAndLoadData() {

        lifecycleScope.launch {
            userProfileInteractor.getCurrentUser().let { result ->
                when (result) {
                    is Result.Success -> {
                        UserCurrent.name = result.data.name
                        UserCurrent.email = result.data.email
                        Log.d("Myregister", "Сохранила object ${result.data.name}")
                        startMainActivity()
                    }
                    is Result.Failure -> {
                        Log.d("Myregister", "Ошибка ${result.exception}")
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                            finish()
                        }, 1500)
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}