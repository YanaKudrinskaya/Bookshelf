package com.yanakudrinskaya.bookshelf

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



const val APP_PREFERENCES = "app_preferences"
const val IS_FIRST_LAUNCH = "is_first_launch"
//const val IS_LOGIN = "is_login"

class MainActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    //private lateinit var btnLogout: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvUserName = findViewById(R.id.text_bottom3)
        //btnLogout = findViewById(R.id.btnLogout)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        val isFirstLauncher = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)

        if (isFirstLauncher) {
            sharedPreferences.edit()
                .putBoolean(IS_FIRST_LAUNCH, false)
                .apply()
            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
        }
        val user = auth.currentUser
        user?.let {
            loadUserData(it.uid)
        }
    }

    private fun loadUserData(userId: String) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("name")
                    tvUserName.text = name

                } else {
                    Toast.makeText(baseContext, "Документ не найден", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(baseContext, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}