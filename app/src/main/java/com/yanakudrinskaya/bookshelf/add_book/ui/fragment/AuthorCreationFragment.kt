package com.yanakudrinskaya.bookshelf.add_book.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.add_book.ui.view_model.AddBookViewModel
import com.yanakudrinskaya.bookshelf.databinding.FragmentAuthorCreationBinding
import com.yanakudrinskaya.bookshelf.library.domain.models.Author
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorCreationFragment : Fragment() {

    private var _binding: FragmentAuthorCreationBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<AddBookViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthorCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val lastName = binding.etLastName.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val middleName = binding.etMiddleName.text.toString()

            if (lastName.isNotEmpty() && firstName.isNotEmpty()) {
                val author = Author(
                    lastName=lastName,
                    firstName = firstName,
                    middleName = middleName)
                viewModel.addAuthor(author)
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}