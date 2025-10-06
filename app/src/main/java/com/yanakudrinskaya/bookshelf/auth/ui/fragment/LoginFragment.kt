package com.yanakudrinskaya.bookshelf.auth.ui.fragment

import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentLoginBinding
import com.yanakudrinskaya.bookshelf.auth.ui.models.RequestStatus
import com.yanakudrinskaya.bookshelf.auth.ui.view_model.LoginViewModel
import com.yanakudrinskaya.bookshelf.root.ui.NavigationVisibilityController
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<LoginViewModel>()

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
    }

    private fun setupObserves() {

        viewModel.getFieldErrorsLiveData().observe(viewLifecycleOwner) { errors ->
            errors?.let { (emailError, passwordError) ->
                // Устанавливаем ошибки для полей
                binding.tilEmail.error = if (emailError.isNotEmpty()) emailError else null
                binding.tilPassword.error = if (passwordError.isNotEmpty()) passwordError else null

                // Меняем цвет рамки (опционально)
                if (emailError.isNotEmpty()) {
                    binding.tilEmail.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.error)
                }
                if (passwordError.isNotEmpty()) {
                    binding.tilPassword.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.error)
                }
            } ?: run {
                // Очищаем ошибки
                binding.tilEmail.error = null
                binding.tilPassword.error = null
                // Восстанавливаем стандартный цвет рамки
//                binding.tilPassword.setBoxStrokeColorStateList(
//                    ContextCompat.getColorStateList(requireContext(), R.color.selector_text_input_stroke)
//                )
//                binding.tilEmail.setBoxStrokeColorStateList(
//                    ContextCompat.getColorStateList(requireContext(), R.color.selector_text_input_stroke)
//                )
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
        // Очищаем ошибки при вводе текста
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.tilEmail.error = null
//                binding.tilEmail.setBoxStrokeColorStateList(
//                    ContextCompat.getColorStateList(requireContext(), R.color.selector_text_input_stroke)
//                )
            }
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.tilPassword.error = null
//                binding.tilPassword.setBoxStrokeColorStateList(
//                    ContextCompat.getColorStateList(requireContext(), R.color.selector_text_input_stroke)
//                )
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

    override fun onDestroyView() {
        (activity as? NavigationVisibilityController)?.setNavigationVisibility(true)
        super.onDestroyView()
        _binding = null
    }
}