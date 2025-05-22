package com.yanakudrinskaya.bookshelf.main.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanakudrinskaya.bookshelf.App
import com.yanakudrinskaya.bookshelf.databinding.ActivityMainBinding
import com.yanakudrinskaya.bookshelf.login.domain.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.main.domain.models.Book
import com.yanakudrinskaya.bookshelf.login.domain.models.UserCurrent
import com.yanakudrinskaya.bookshelf.login.ui.activity.LoginActivity
import com.yanakudrinskaya.bookshelf.main.BookListAdapter
import com.yanakudrinskaya.bookshelf.main.ui.view_model.MainViewModel
import com.yanakudrinskaya.bookshelf.settings.ui.activity.SettingsActivity
import com.yanakudrinskaya.bookshelf.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainViewModel>()
    private val settingsViewModel  by viewModel<SettingsViewModel>()

    private val bookListAdapter = BookListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUserData()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUserData() {
        binding.settingsLabel.text = UserCurrent.name
        binding.rvItems.adapter = bookListAdapter
    }

    private fun setupObservers() {

        settingsViewModel.getLogoutEvent().observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                performLogout()
            }
        }
    }

    private fun performLogout() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun setupClickListeners() {
        binding.addBtn.setOnClickListener{
            //открыть карточку добавления новой книги
        }
        binding.settingsBtn.setOnClickListener {
            startActivityWithAnimation(SettingsActivity::class.java)
        }
        bookListAdapter.onItemClick = { book -> openBook(book) }
    }

    private fun startActivityWithAnimation(activity: Class<*>) {
        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
        startActivity(Intent(this, activity), options.toBundle())
    }
    private fun openBook(book: Book) {
        //открываем активити Book
    }
}