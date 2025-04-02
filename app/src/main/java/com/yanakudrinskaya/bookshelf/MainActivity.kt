package com.yanakudrinskaya.bookshelf

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.yanakudrinskaya.bookshelf.MyApp.Companion.APP_PREFERENCES
import com.yanakudrinskaya.bookshelf.MyApp.Companion.IS_FIRST_LAUNCH
import com.yanakudrinskaya.bookshelf.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val app by lazy { application as MyApp }
    private val sharedViewModel by lazy {
        (application as MyApp).sharedViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge()
        checkFirstLaunch()
        setupUserData()
        setupObservers()
        setupClickListeners()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkFirstLaunch() {
        val prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        if (prefs.getBoolean(IS_FIRST_LAUNCH, true)) {
            prefs.edit().putBoolean(IS_FIRST_LAUNCH, false).apply()
            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
        }
    }

    private fun setupUserData() {
        app.auth.currentUser?.let { user ->
            loadUserData(user.uid)
            loadAvatar()
        }
    }

    private fun loadUserData(userId: String) {
        app.firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                document?.getString("name")?.let {
                    User.userName = it
                } ?: run {
                    redirectToLogin()
                }
            }
            .addOnFailureListener { e ->
                showError("Ошибка: ${e.message}")
            }
    }

    private fun loadAvatar() {
        MyApp.AvatarManager.loadAvatar(this)?.let {
            binding.settingsBtn.setImageBitmap(it)
            User.avatarBitmap = it
        }
    }
    private fun setupObservers() {
        sharedViewModel.logoutEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { shouldLogout ->
                if (shouldLogout) performLogout()
            }
        }
    }

    private fun performLogout() {
        app.auth.signOut()
        MyApp.AvatarManager.deleteAvatar(this)
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

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}