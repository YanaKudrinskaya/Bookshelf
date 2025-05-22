package com.yanakudrinskaya.bookshelf.boarding.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanakudrinskaya.bookshelf.boarding.ui.model.BoardingNavigation
import com.yanakudrinskaya.bookshelf.boarding.ui.view_model.OnBoardingViewModel
import com.yanakudrinskaya.bookshelf.databinding.ActivityOnBoardingBinding
import com.yanakudrinskaya.bookshelf.login.ui.activity.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private val viewModel by viewModel<OnBoardingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.getBoardingNavigationLiveData().observe(this) { content ->
            when(content) {
                is BoardingNavigation.Content -> setupView(content)
                is BoardingNavigation.Close -> startLoginActivity()
            }
        }
    }

    private fun setupView(content: BoardingNavigation.Content) {
        binding.onBoardingText.text = content.content
    }

    private fun setupClickListeners() {
        binding.nextBtn.setOnClickListener {

            viewModel.getNextText()
        }

        binding.skipBtn.setOnClickListener {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}