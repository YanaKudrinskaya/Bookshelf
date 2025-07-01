package com.yanakudrinskaya.bookshelf.splash.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentSplashBinding
import com.yanakudrinskaya.bookshelf.splash.ui.model.NavigationEvent
import com.yanakudrinskaya.bookshelf.splash.ui.view_model.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashFragment : Fragment() {

    companion object {
        private const val DELAY = 1500L
    }

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getNavigationLiveData().observe(viewLifecycleOwner) { event ->
            when (event) {
                NavigationEvent.FIRST -> startFragment(R.id.action_splashFragment_to_onBoardingFragment)
                NavigationEvent.MAIN -> startFragment(R.id.action_to_main)
                NavigationEvent.LOGIN -> startFragment(R.id.action_splashFragment_to_auth_graph)
            }
        }
    }

    private fun startFragment(destination: Int) {
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(destination)
        }, DELAY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}