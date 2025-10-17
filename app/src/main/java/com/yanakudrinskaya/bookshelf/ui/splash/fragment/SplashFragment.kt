package com.yanakudrinskaya.bookshelf.ui.splash.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentSplashBinding
import com.yanakudrinskaya.bookshelf.ui.root.NavigationVisibilityController
import com.yanakudrinskaya.bookshelf.ui.splash.models.NavigationEvent
import com.yanakudrinskaya.bookshelf.ui.splash.view_model.SplashViewModel
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