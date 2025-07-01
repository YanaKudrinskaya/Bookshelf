package com.yanakudrinskaya.bookshelf.root.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.ActivityRootBinding
import com.yanakudrinskaya.bookshelf.root.ui.view_model.RootViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity() {

    companion object {
        private const val DELAY = 200L
    }

    private lateinit var binding: ActivityRootBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private val viewModel by viewModel<RootViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getNavigationEvents().observe(this) { event ->
                bottomNavigationViewVisible(event)
        }
    }

    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupListeners() {

        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.changeDestination(destination.id)
        }
    }

    private fun bottomNavigationViewVisible(visible: Boolean) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.bottomNavigationView.isVisible = visible
        }, DELAY)
    }
}