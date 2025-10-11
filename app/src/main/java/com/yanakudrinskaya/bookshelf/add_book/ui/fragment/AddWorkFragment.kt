package com.yanakudrinskaya.bookshelf.add_book.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.add_book.ui.models.AddWorkScreenState
import com.yanakudrinskaya.bookshelf.add_book.ui.view_model.AddBookViewModel
import com.yanakudrinskaya.bookshelf.databinding.FragmentAddWorkBinding
import com.yanakudrinskaya.bookshelf.library.domain.models.Author
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddWorkFragment : Fragment() {
    private var _binding: FragmentAddWorkBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AddBookViewModel>()

    private lateinit var authorAdapter: ArrayAdapter<Author>
    private lateinit var worksAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()
        setupObservers()
    }

    private fun setupAdapters() {
        authorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
        worksAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)

        binding.actAuthor.setAdapter(authorAdapter)
        binding.actWorkTitle.setAdapter(worksAdapter)
    }

    private fun setupListeners() {
        binding.actAuthor.setOnItemClickListener { _, _, position, _ ->
            val selectedAuthor = authorAdapter.getItem(position)
            selectedAuthor?.let { author ->
                viewModel.selectAuthorToWork(author)
                viewModel.searchWorksByAuthor(author.id)
            }
        }

        binding.btnNewAuthor.setOnClickListener {

        }

        binding.btnSave.setOnClickListener {
            val authorText = binding.actAuthor.text.toString()
            val workTitle = binding.actWorkTitle.text.toString()

            if (authorText.isNotEmpty() && workTitle.isNotEmpty()) {
                Log.d("MyLibrary", "Сохраняем произведение в книгу")
                viewModel.saveWork(title = workTitle)
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.getAddWorkStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddWorkScreenState.WorksSearchResults -> showWorks(state.works)
                AddWorkScreenState.AuthorsIsEmpty -> Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showWorks(works: List<Work>) {
        val items = works.map { "${it.title} ${it.authors}" }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите произведение автора")
            .setItems(items) { _, which ->
                Log.d("MyLibrary", "Выбрано произведение ${works[which]}")
                //viewModel.selectWork(works[which])
            }
            .setNegativeButton("Новое произведение") { _, _ ->
                Log.d("MyLibrary", "Добавляем новое название произведения")
                //findNavController().navigate(R.id.action_addBookFragment_to_authorCreationFragment)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}