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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var registerBtn: TextView
    private lateinit var loginBtn: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity)) { v, insets ->
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

        etEmail = findViewById(R.id.email)
        etPassword = findViewById(R.id.pass)
        registerBtn = findViewById(R.id.register_btn)
        loginBtn = findViewById(R.id.login_btn)

        auth = Firebase.auth

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        loginBtn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(baseContext, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Myregister", "Авторизация прошла успешно")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.d("Myregister", "Ошибка авторизации")
                    Toast.makeText(baseContext,"Authentication failed.",Toast.LENGTH_SHORT,).show()
                }
            }
    }
}