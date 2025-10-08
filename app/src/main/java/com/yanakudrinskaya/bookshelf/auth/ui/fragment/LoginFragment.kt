package com.yanakudrinskaya.bookshelf.auth.ui.fragment

import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentLoginBinding
import com.yanakudrinskaya.bookshelf.auth.ui.models.RequestStatus
import com.yanakudrinskaya.bookshelf.auth.ui.view_model.LoginViewModel
import com.yanakudrinskaya.bookshelf.auth.utils.GoogleCredentialManager
import com.yanakudrinskaya.bookshelf.root.ui.NavigationVisibilityController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<LoginViewModel>()

    private val googleCredentialManager by lazy {
        GoogleCredentialManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObserves()
        setupTextWatchers()
    }

    override fun onResume() {
        super.onResume()
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(false)
    }

    private fun setupListeners() {

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.processRequest(email, password)
        }

        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun setupObserves() {

        viewModel.getFieldErrorsLiveData().observe(viewLifecycleOwner) { errors ->
            errors?.let { (emailError, passwordError) ->

                binding.tilEmail.error = if (emailError.isNotEmpty()) emailError else null
                binding.tilPassword.error = if (passwordError.isNotEmpty()) passwordError else null

                if (emailError.isNotEmpty()) {
                    binding.tilEmail.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.error)
                }
                if (passwordError.isNotEmpty()) {
                    binding.tilPassword.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.error)
                }
            } ?: run {
                binding.tilEmail.error = null
                binding.tilPassword.error = null
            }
        }

        viewModel.getRequestStatusLiveData().observe(viewLifecycleOwner) { status ->
            when (status) {
                is RequestStatus.Error -> {
                    showError(status.message)
                }

                is RequestStatus.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_navigation_graph)
                }
            }
        }
    }

    private fun setupTextWatchers() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.tilEmail.error = null
            }
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.tilPassword.error = null
            }
        })
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun signInWithGoogle() {
        lifecycleScope.launch {
            showLoading(true)
            val idToken = googleCredentialManager.signInWithGoogle(requireActivity())

            if (idToken != null) {
                viewModel.signInWithGoogle(idToken)
            } else {
                showError("Google sign in failed")
                showLoading(false)
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.signInButton.isEnabled = !show
    }
    override fun onDestroyView() {
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(true)
        super.onDestroyView()
        _binding = null
    }
}