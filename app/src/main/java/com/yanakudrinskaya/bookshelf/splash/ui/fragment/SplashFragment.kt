package com.yanakudrinskaya.bookshelf.splash.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentSplashBinding
import com.yanakudrinskaya.bookshelf.root.ui.NavigationVisibilityController
import com.yanakudrinskaya.bookshelf.splash.ui.models.NavigationEvent
import com.yanakudrinskaya.bookshelf.splash.ui.view_model.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SplashFragment : Fragment() {

    private  var _binding: FragmentSplashBinding? = null
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

    override fun onResume() {
        super.onResume()
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(false)
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
            findNavController().navigate(destination)
    }

    override fun onDestroyView() {
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(true)
        super.onDestroyView()
        _binding = null
    }
}