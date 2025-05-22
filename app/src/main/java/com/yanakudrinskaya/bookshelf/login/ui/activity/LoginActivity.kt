package com.yanakudrinskaya.bookshelf.login.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.ActivityLoginBinding
import com.yanakudrinskaya.bookshelf.login.domain.models.Content
import com.yanakudrinskaya.bookshelf.login.ui.models.EditStatus
import com.yanakudrinskaya.bookshelf.login.ui.models.RequestStatus
import com.yanakudrinskaya.bookshelf.login.ui.models.ScreenState
import com.yanakudrinskaya.bookshelf.login.ui.view_model.LoginViewModel
import com.yanakudrinskaya.bookshelf.main.ui.activity.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

//    private val viewModel: LoginViewModel by viewModels {
//        LoginViewModel.getViewModelFactory()
//    }
    private val viewModel by viewModel<LoginViewModel>()
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

        setupListeners()
        setupObserves()
    }


    private fun setupListeners() {

        binding.changeContentBtn.setOnClickListener {
            viewModel.changeContent()
        }

        binding.button.setOnClickListener {
            val name = binding.userName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confPass = binding.confPassword.text.toString()
            viewModel.processRequest(name, email, password, confPass)
        }

        binding.userName.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }
        binding.email.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }
        binding.password.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }
        binding.confPassword.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }
    }

    private fun setupObserves() {

        viewModel.getPlayStatusLiveData().observe(this) { screenState ->
            when (screenState.screenState) {
                ScreenState.LOGIN -> setupLoginUI(screenState.content!!)
                ScreenState.REGISTER -> setupRegisterUI(screenState.content!!)
            }
        }

        viewModel.getRequestStatusLiveData().observe(this) { status ->
            when (status) {
                is RequestStatus.Error -> {
                    showError(status)
                }

                is RequestStatus.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun showError(status: RequestStatus.Error) {
        when (status.status) {
            EditStatus.NONE -> {}
            EditStatus.NAME -> {errorEdit(binding.userName)}
            EditStatus.EMAIL -> {errorEdit(binding.email)}
            EditStatus.PASSWORD, EditStatus.CONFPASSWORD -> {errorEdit(binding.password)}
        }
        Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
    }

    private fun errorEdit(edit: EditText) {
        edit.backgroundTintList = ContextCompat.getColorStateList(this, R.color.error)
        edit.setText("")
        binding.confPassword.setText("")
    }

    private fun cleanError() {
        binding.userName.backgroundTintList = null
        binding.email.backgroundTintList = null
        binding.password.backgroundTintList = null
        binding.password.backgroundTintList = null
    }


    private fun setupLoginUI(content: Content) {
        binding.secondaryText.text = content.title
        binding.userName.isVisible = false
        binding.confPassword.isVisible = false
        binding.button.text = content.button
        binding.changeContentBtn.text = content.bottomButton
    }

    private fun setupRegisterUI(content: Content) {
        binding.secondaryText.text = content.title
        binding.userName.isVisible = true
        binding.confPassword.isVisible = true
        binding.button.text = content.button
        binding.changeContentBtn.text = content.bottomButton
    }
}