package com.yanakudrinskaya.bookshelf

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var confPass: EditText
    private lateinit var registerBtn: TextView
    private lateinit var loginBtn: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_activity)) { v, insets ->
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

        userName = findViewById(R.id.name_edit)
        email = findViewById(R.id.email_edit)
        pass = findViewById(R.id.pass_edit)
        confPass = findViewById(R.id.conf_pass)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        registerBtn = findViewById(R.id.register_btn)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        registerBtn.setOnClickListener {
            val name = userName.text.toString()
            val emailString = email.text.toString()
            val password = pass.text.toString()
            val confPassText = confPass.text.toString()

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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Myregister", "Регистрация прошла успешно")
                    val user = auth.currentUser
                    user?.let {
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email
                        )
                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(baseContext, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                                // Переход на главный экран
                            }
                            .addOnFailureListener { e ->
                                showInfoAlert("Ошибка: ${e.message}")
                            }
                    }
                } else {
                    Log.d("Myregister", "Неверные адрес электронной почты или пароль", task.exception)
                    showInfoAlert("Ошибка: ${task.exception?.message}")
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