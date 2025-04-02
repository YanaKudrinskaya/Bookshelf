package com.yanakudrinskaya.bookshelf

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanakudrinskaya.bookshelf.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val app by lazy { application as MyApp }

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
            }
            else if (TextUtils.isEmpty(emailString)) {
                showInfoAlert("Введите ваш e_mail")
            }
            else if (password.length < 5) {
                showInfoAlert("Пароль должен быть не менее 5 символов")
            }
            else if (confPassText != password) {
                showInfoAlert("Пароли не совпадают")
            }
            else  registration(name, emailString, password)
        }
    }

    private fun registration(name: String, email: String, password: String) {
        app.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Myregister", "Регистрация прошла успешно")
                    val user = app.auth.currentUser
                    user?.let {
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email
                        )
                        app.firestore.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    baseContext,
                                    "Регистрация успешна",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Переход на главный экран
                            }
                            .addOnFailureListener { e ->
                                showInfoAlert("Ошибка: ${e.message}")
                            }
                    }
                }
            }
    }

//    private fun registration(name: String, email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Log.d("Myregister", "Регистрация прошла успешно")
//                    val user = auth.currentUser
//                    user?.let {
//                        val userData = hashMapOf(
//                            "name" to name,
//                            "email" to email
//                        )
//                        db.collection("users").document(user.uid)
//                            .set(userData)
//                            .addOnSuccessListener {
//                                Toast.makeText(baseContext, "Регистрация успешна", Toast.LENGTH_SHORT).show()
//                                // Переход на главный экран
//                            }
//                            .addOnFailureListener { e ->
//                                showInfoAlert("Ошибка: ${e.message}")
//                            }
//                    }
//                } else {
//                    Log.d("Myregister", "Неверные адрес электронной почты или пароль", task.exception)
//                    showInfoAlert("Ошибка: ${task.exception?.message}")
//                }
//            }
//    }
//
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