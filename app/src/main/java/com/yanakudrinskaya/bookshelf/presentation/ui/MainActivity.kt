package com.yanakudrinskaya.bookshelf.presentation.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanakudrinskaya.bookshelf.App
import com.yanakudrinskaya.bookshelf.Creator
import com.yanakudrinskaya.bookshelf.databinding.ActivityMainBinding
import com.yanakudrinskaya.bookshelf.domain.interactors.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.domain.models.UserCurrent


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel by lazy {
        (application as App).sharedViewModel
    }
    private lateinit var userProfileInteractor: UserProfileInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userProfileInteractor = Creator.provideUserProfileInteractor()

        setupUserData()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUserData() {
        binding.settingsLabel.text = UserCurrent.name
    }

    private fun setupObservers() {
        sharedViewModel.logoutEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { shouldLogout ->
                if (shouldLogout) performLogout()
            }
        }
    }

    private fun performLogout() {
        userProfileInteractor.logout()
        App.AvatarManager.deleteAvatar(this)
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun setupClickListeners() {
        binding.settingsBtn.setOnClickListener {
            startActivityWithAnimation(SettingsActivity::class.java)
        }
    }

    private fun startActivityWithAnimation(activity: Class<*>) {
        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
        startActivity(Intent(this, activity), options.toBundle())
    }
}