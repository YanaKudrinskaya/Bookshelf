package com.yanakudrinskaya.bookshelf.ui.auth.fragment

import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.models.GoogleSignInResult
import com.yanakudrinskaya.bookshelf.databinding.FragmentLoginBinding
import com.yanakudrinskaya.bookshelf.ui.auth.models.LoginUiState
import com.yanakudrinskaya.bookshelf.ui.auth.view_model.LoginViewModel
import com.yanakudrinskaya.bookshelf.data.auth.network.google_auth.GoogleCredentialManager
import com.yanakudrinskaya.bookshelf.ui.root.NavigationVisibilityController
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModel()

    private val googleCredentialManager: GoogleCredentialManager by inject()

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
        setupObservers()
        setupTextWatchers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (validateForm(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        binding.yandexSignInButton.setOnClickListener {
            signInWithYandex()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.getUiState().collect { state ->
                when (state) {
                    is LoginUiState.Loading -> showLoading(true)
                    is LoginUiState.Success -> {
                        findNavController().navigate(R.id.action_loginFragment_to_navigation_graph)
                        viewModel.clearError()
                    }
                    is LoginUiState.Error -> {
                        showLoading(false)
                        showError(state.message)
                        viewModel.clearError()
                    }
                    is LoginUiState.Idle -> showLoading(false)
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var isValid = true
        binding.apply {
            if (email.isEmpty()) {
                tilEmail.error = requireContext().getString(R.string.enter_email)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = requireContext().getString(R.string.invalid_email)
                isValid = false
            } else {
                tilEmail.error = null
            }

            if (password.isEmpty()) {
                tilPassword.error = requireContext().getString(R.string.enter_password)
                isValid = false
            } else {
                tilPassword.error = null
            }
        }
        return isValid
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                clearErrors()
            }
        }

        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
    }

    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
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
            when (val result = googleCredentialManager.signInWithGoogle(requireActivity())) {
                is GoogleSignInResult.Success -> {
                    viewModel.signInWithGoogle(result.idToken)
                }
                is GoogleSignInResult.Error -> {
                    showError(result.message)
                    showLoading(false)
                }
            }
        }
    }

    private fun signInWithYandex() {
        lifecycleScope.launch {
            showLoading(true)
            viewModel.signInWithYandex()
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.contentGroup.isVisible = !show
    }

    override fun onResume() {
        super.onResume()
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(false)
    }

    override fun onDestroyView() {
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(true)
        super.onDestroyView()
        _binding = null
    }
}