package com.yanakudrinskaya.bookshelf.add_book.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.add_book.ui.ContentAdapter
import com.yanakudrinskaya.bookshelf.add_book.ui.models.AddBookScreenState
import com.yanakudrinskaya.bookshelf.add_book.ui.models.NavigateFragmentEvent
import com.yanakudrinskaya.bookshelf.add_book.ui.view_model.AddBookViewModel
import com.yanakudrinskaya.bookshelf.databinding.FragmentAddBookBinding
import com.yanakudrinskaya.bookshelf.library.domain.models.Author
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddBookFragment : Fragment() {

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<AddBookViewModel>()
    private val contentAdapter = ContentAdapter()

    private var isAnthology = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBookBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBookContent.adapter = contentAdapter

        setupListeners()
        setupObserves()
    }

    private fun setupObserves() {
        viewModel.getAddBookStateLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AddBookScreenState.Content -> updateContentList(it.content)
                is AddBookScreenState.Error -> showError(it.e)
                is AddBookScreenState.Loading -> showLoading()
                is AddBookScreenState.Success -> showCloseMessage()
                is AddBookScreenState.NavigateFragment ->
                    when (it.fragment) {
                        NavigateFragmentEvent.CONTENT -> {}
                        NavigateFragmentEvent.AUTHOR -> Log.d("MyLibrary", "Автор не указан")
                    }

                is AddBookScreenState.AuthorSearchResults -> showAuthorSuggestions(it.authors)
                is AddBookScreenState.AuthorSelected -> updateAuthorField(it.author)
            }
        }
    }

    private fun showLoading() {
        binding.contentGroup.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showError(e: String) {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.VISIBLE
        Toast.makeText(requireContext(), e, Toast.LENGTH_SHORT).show()
    }

    private fun showCloseMessage() {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.GONE
        binding.tvCloseMessage.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigateUp()
        }, 1500)
    }

    private fun setupListeners() {

        binding. fabAddWork.setOnClickListener {       }

        binding.btnSaveBook.setOnClickListener {
            saveBook()
        }

        binding.etDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val input = p0.toString()
                if (input.length == 4) {
                    val year = input.toIntOrNull()
                    if (year == null || year < 1900 || year > 2100) {
                        binding.tilDate.error = "Введите корректный год (1900-2100)"
                    } else {
                        binding.tilDate.error = null
                    }
                }
            }

        })

        binding.etAuthor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.length > 2) {
                        viewModel.searchAuthors(it.toString())
                    }
                }
            }
        })
    }

    private fun updateContentList(list: MutableList<Work>) {
        contentAdapter.contentList = list
        contentAdapter.notifyDataSetChanged()
        if (list.isEmpty()) {
            binding.tvContentHint.visibility = View.VISIBLE
        } else {
            binding.tvContentHint.visibility = View.GONE
        }
    }

    private fun saveBook() {
        val author = binding.etAuthor.text.toString()
        val title = binding.etTitle.text.toString()
        val publisher = binding.etPublisher.text.toString()
        val year = binding.etDate.text.toString()

        viewModel.saveBook(author, title, publisher, year)
    }

    private fun showAuthorSuggestions(authors: List<Author>) {
        val items = authors.map { "${it.lastName} ${it.firstName} ${it.middleName}" }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите автора")
            .setItems(items) { _, which ->
                viewModel.selectAuthor(authors[which])
            }
            .setNegativeButton("Новый автор") { _, _ ->

            }
    }

    private fun updateAuthorField(author: Author) {
        binding.etAuthor.setText("${author.lastName} ${author.firstName} ${author.middleName}".trim())
        binding.etAuthor.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}