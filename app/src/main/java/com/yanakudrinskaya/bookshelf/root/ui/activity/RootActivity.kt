package com.yanakudrinskaya.bookshelf.root.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.ActivityRootBinding
import com.yanakudrinskaya.bookshelf.root.ui.NavigationVisibilityController
import com.yanakudrinskaya.bookshelf.root.ui.view_model.RootViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity(), NavigationVisibilityController {

    private lateinit var binding: ActivityRootBinding
    private val viewModel by viewModel<RootViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Bookshelf)
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        viewModel.getNavigationEvents().observe(this) { isVisible ->
            bottomNavigationView.isVisible = isVisible
        }
    }

    override fun setNavigationVisibility(visible: Boolean) {
        viewModel.setNavigationVisible(visible)
    }
}