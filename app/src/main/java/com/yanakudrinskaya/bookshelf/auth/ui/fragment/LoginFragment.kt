package com.yanakudrinskaya.bookshelf.auth.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentLoginBinding
import com.yanakudrinskaya.bookshelf.auth.ui.models.EditStatus
import com.yanakudrinskaya.bookshelf.auth.ui.models.RequestStatus
import com.yanakudrinskaya.bookshelf.auth.ui.view_model.LoginViewModel
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

    }

    private fun setupListeners() {

        binding.tvChangeContent.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvButton.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.edPassword.text.toString()
            viewModel.processRequest(email, password)
        }

        binding.etEmail.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }
        binding.edPassword.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }
    }

    private fun setupObserves() {

        viewModel.getRequestStatusLiveData().observe(viewLifecycleOwner) { status ->
            when (status) {
                is RequestStatus.Error -> {
                    showError(status)
                }

                is RequestStatus.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_navigation_graph)
                }
            }
        }
    }

    private fun showError(status: RequestStatus.Error) {
        when (status.status) {
            EditStatus.NONE -> {}
            EditStatus.NAME -> {}

            EditStatus.EMAIL -> {
                errorEdit(binding.etEmail)
            }

            EditStatus.PASSWORD, EditStatus.CONFPASSWORD -> {
                errorEdit(binding.edPassword)
            }
        }
        Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
    }

    private fun errorEdit(edit: EditText) {
        edit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.error)
        editTextClean()
    }

    private fun cleanError() {
        binding.etEmail.backgroundTintList = null
        binding.edPassword.backgroundTintList = null
    }

    private fun editTextClean() {
        binding.etEmail.setText("")
        binding.edPassword.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}