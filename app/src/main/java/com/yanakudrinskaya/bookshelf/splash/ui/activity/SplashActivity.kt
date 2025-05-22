package com.yanakudrinskaya.bookshelf.splash.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanakudrinskaya.bookshelf.boarding.ui.activity.OnBoardingActivity
import com.yanakudrinskaya.bookshelf.login.ui.activity.LoginActivity
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.main.ui.activity.MainActivity
import com.yanakudrinskaya.bookshelf.splash.ui.model.NavigationEvent
import com.yanakudrinskaya.bookshelf.splash.ui.view_model.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getNavigationLiveData().observe(this) { event ->
            when (event) {
                NavigationEvent.FIRST -> startActivity(OnBoardingActivity::class.java)
                NavigationEvent.MAIN -> startActivity(MainActivity::class.java)
                NavigationEvent.LOGIN -> startActivity(LoginActivity::class.java)
            }
        }
    }

    private fun startActivity(activity: Class<out Activity>) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, activity))
            finish()
        }, 1500)
    }
}