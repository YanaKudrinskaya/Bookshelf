package com.yanakudrinskaya.bookshelf.on_boarding.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.on_boarding.ui.model.BoardingNavigation
import com.yanakudrinskaya.bookshelf.databinding.FragmentOnBoardingBinding
import com.yanakudrinskaya.bookshelf.on_boarding.ui.view_model.OnBoardingViewModel
import com.yanakudrinskaya.bookshelf.root.ui.NavigationVisibilityController
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<OnBoardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
            : View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener { viewModel.getNextText() }
        binding.btnSkip.setOnClickListener { startLoginFragment() }

        viewModel.getBoardingNavigationLiveData().observe(viewLifecycleOwner) { navigation ->
            when(navigation) {
                is BoardingNavigation.Content -> binding.tvOnBoarding.text = navigation.content
                is BoardingNavigation.Close -> startLoginFragment()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(false)
    }
    private fun startLoginFragment() {
        findNavController().navigate(R.id.action_onBoardingFragment_to_auth_graph)
    }

    override fun onDestroyView() {
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(true)
        super.onDestroyView()
        _binding = null
    }
}