package com.yanakudrinskaya.bookshelf.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.yanakudrinskaya.bookshelf.Creator
import com.yanakudrinskaya.bookshelf.databinding.ActivityRegisterBinding
import com.yanakudrinskaya.bookshelf.domain.interactors.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.domain.models.Result
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var userProfileInteractor: UserProfileInteractor
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
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
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.registerBtn.setOnClickListener {
            val name = binding.userName.text.toString()
            val emailString = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confPassText = binding.confPassword.text.toString()

            if (TextUtils.isEmpty(name)) {
                showInfoAlert("Введите ваше имя")
            } else if (TextUtils.isEmpty(emailString)) {
                showInfoAlert("Введите ваш e_mail")
            } else if (password.length < 5) {
                showInfoAlert("Пароль должен быть не менее 5 символов")
            } else if (confPassText != password) {
                showInfoAlert("Пароли не совпадают")
            } else registration(name, emailString, password)
        }
    }

    private fun registration(name: String, email: String, password: String) {
        lifecycleScope.launch {
            userProfileInteractor.register(name, email, password).let { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("Myregister", "Регистрация прошла успешно")
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }

                    is Result.Failure -> {
                        Log.d("Myregister", "Ошибка: ${result.exception.message}")
                        showInfoAlert("Ошибка: ${result.exception.message}")
                    }
                }
            }
        }
    }

    private fun showInfoAlert(text: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ошибка авторизации")
            .setMessage(text)
            .setCancelable(false)
            .setPositiveButton(
                "Далее"
            ) { dialogInterface, i -> dialogInterface.cancel() }
        val dialog = builder.create()
        dialog.show()
    }
}