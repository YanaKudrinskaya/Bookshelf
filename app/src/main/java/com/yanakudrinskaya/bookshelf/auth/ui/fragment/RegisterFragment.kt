package com.yanakudrinskaya.bookshelf.auth.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.auth.ui.models.RegisterUiState
import com.yanakudrinskaya.bookshelf.auth.ui.view_model.RegisterViewModel
import com.yanakudrinskaya.bookshelf.databinding.FragmentRegisterBinding
import com.yanakudrinskaya.bookshelf.root.ui.NavigationVisibilityController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        setupTextWatchers()
    }

    private fun setupListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (validateForm(name, email, password, confirmPassword)) {
                    viewModel.register(name, email, password)
                }
            }

            tvLogin.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.getUiState().collect { state ->
                when (state) {
                    is RegisterUiState.Loading -> showLoading(true)
                    is RegisterUiState.Success -> {
                        showLoading(false)
                        findNavController().navigate(R.id.action_registerFragment_to_navigation_graph)
                        viewModel.clearError()
                    }

                    is RegisterUiState.Error -> {
                        showLoading(false)
                        showError(state.message)
                        viewModel.clearError()
                    }

                    is RegisterUiState.Idle -> showLoading(false)
                }
            }
        }
    }

    private fun validateForm(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true
        binding.apply {
            if (name.isEmpty()) {
                tilName.error = requireContext().getString(R.string.enter_name)
                isValid = false
            } else {
                tilName.error = null
            }

            if (email.isEmpty()) {
                tilEmail.error = requireContext().getString(R.string.enter_email)
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = requireContext().getString(R.string.invalid_email)
                isValid = false
            } else {
                tilEmail.error = null
            }

            if (password.isEmpty()) {
                tilPassword.error = requireContext().getString(R.string.enter_password)
                isValid = false
            } else if (password.length < 6) {
                tilPassword.error = requireContext().getString(R.string.password_valide)
            } else {
                tilPassword.error = null
            }

            if (confirmPassword.isEmpty()) {
                tilConfirmPassword.error = requireContext().getString(R.string.password_hint)
                isValid = false
            } else if (password != confirmPassword) {
                tilConfirmPassword.error = requireContext().getString(R.string.password_not_match)
                isValid = false
            } else {
                tilConfirmPassword.error = null
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

        binding.etName.addTextChangedListener(textWatcher)
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
        binding.etConfirmPassword.addTextChangedListener(textWatcher)
    }

    private fun clearErrors() {
        binding.apply {
            tilName.error = null
            tilEmail.error = null
            tilPassword.error = null
            tilConfirmPassword.error = null
        }
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
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