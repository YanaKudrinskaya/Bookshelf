package com.yanakudrinskaya.bookshelf

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var skipBtn: TextView
    private lateinit var nextBtn: TextView
    private lateinit var textOnBoarding: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_on_boarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.on_boarding)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        skipBtn = findViewById(R.id.skip_button)
        nextBtn = findViewById(R.id.next_button)
        textOnBoarding = findViewById(R.id.on_boarding_text)

        val welcomeTextList = arrayListOf(
            getString(R.string.on_boarding_text_1),
            getString(R.string.on_boarding_text_2),
            getString(R.string.on_boarding_text_3)
        )

        textOnBoarding.text = welcomeTextList[0]
        var currentTextIndex = 0

        nextBtn.setOnClickListener {
            if(currentTextIndex < welcomeTextList.size) {
                currentTextIndex++
                textOnBoarding.text = welcomeTextList[currentTextIndex]
            } else {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
        }

        skipBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}