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
import com.yanakudrinskaya.bookshelf.databinding.FragmentRegisterBinding
import com.yanakudrinskaya.bookshelf.auth.ui.models.EditStatus
import com.yanakudrinskaya.bookshelf.auth.ui.models.RequestStatus
import com.yanakudrinskaya.bookshelf.auth.ui.view_model.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<RegisterViewModel>()

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
        setupObserves()

    }

    private fun setupListeners() {

        binding.tvloginBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvButton.setOnClickListener {
            val name = binding.etUserName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confPassword = binding.etConfPassword.text.toString()
            viewModel.processRequest(name, email, password, confPassword)
        }
        binding.etUserName.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }

        binding.etEmail.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }
        binding.etPassword.setOnFocusChangeListener { view, hasFocus ->
            cleanError()
        }

        binding.etConfPassword.setOnFocusChangeListener { view, hasFocus ->
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
                    findNavController().navigate(R.id.action_registerFragment_to_navigation_graph)
                }
            }
        }
    }

    private fun showError(status: RequestStatus.Error) {
        when (status.status) {
            EditStatus.NONE -> {}
            EditStatus.NAME -> {
                errorEdit(binding.etUserName)
            }

            EditStatus.EMAIL -> {
                errorEdit(binding.etEmail)
            }

            EditStatus.PASSWORD, EditStatus.CONFPASSWORD -> {
                errorEdit(binding.etPassword)
            }
        }
        Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
    }

    private fun errorEdit(edit: EditText) {
        edit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.error)
        editTextClean()
    }

    private fun cleanError() {
        binding.etUserName.backgroundTintList = null
        binding.etEmail.backgroundTintList = null
        binding.etPassword.backgroundTintList = null
        binding.etConfPassword.backgroundTintList = null
    }

    private fun editTextClean() {
        binding.etUserName.setText("")
        binding.etEmail.setText("")
        binding.etPassword.setText("")
        binding.etConfPassword.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}