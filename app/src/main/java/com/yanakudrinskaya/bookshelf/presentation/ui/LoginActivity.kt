package com.yanakudrinskaya.bookshelf.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.yanakudrinskaya.bookshelf.domain.models.Result
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.yanakudrinskaya.bookshelf.Creator
import com.yanakudrinskaya.bookshelf.databinding.ActivityLoginBinding
import com.yanakudrinskaya.bookshelf.domain.interactors.UserProfileInteractor
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var userProfileInteractor: UserProfileInteractor

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = if (ime.bottom > 0) ime.bottom else systemBars.bottom
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPadding
            )
            insets
        }

        userProfileInteractor = Creator.provideUserProfileInteractor()

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(baseContext, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            userProfileInteractor.login(email, password).let { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("Myregister", "Авторизация прошла успешно")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                    is Result.Failure -> {
                        Log.d("Myregister", "Ошибка авторизации")
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }
    }
}