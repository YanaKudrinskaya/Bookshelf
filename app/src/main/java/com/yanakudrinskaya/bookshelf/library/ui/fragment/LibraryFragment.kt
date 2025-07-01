package com.yanakudrinskaya.bookshelf.library.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentLibraryBinding
import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.ui.adapters.BookListAdapter
import com.yanakudrinskaya.bookshelf.library.ui.model.LibraryState
import com.yanakudrinskaya.bookshelf.library.ui.view_model.LibraryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<LibraryViewModel>()

    private val bookListAdapter = BookListAdapter()
    private lateinit var simpleTextWatcher: TextWatcher

    private var searchString: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupUserData()
        setupView()
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.getLibraryLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun setupView() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.fabAddBook.isVisible = true
        }, 200)
    }

    private fun setupListeners() {

        binding.fabAddBook.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_addBookFragment)
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClearBtn.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchString = binding.search.text.toString()

            }
        }

        simpleTextWatcher.let { binding.search.addTextChangedListener(it) }

        binding.search.setOnFocusChangeListener { view, hasFocus ->
            binding.fabAddBook.isVisible =
                if (hasFocus) false else true
        }

        binding.ivClearBtn.setOnClickListener {
            binding.search.setText("")
        }

        bookListAdapter.onItemClick = { book -> openBook(book) }

    }

    private fun openBook(book: Book) {
        findNavController().navigate(R.id.action_libraryFragment_to_bookFragment)
    }


    private fun setupUserData() {
        binding.rvItems.adapter = bookListAdapter
        viewModel.loadLibrary()
    }

    private fun render(state: LibraryState) {
        when (state) {
            is LibraryState.Content -> showLibaryContent(state.books)
            is LibraryState.Empty -> binding.tvLibraryEmpty.visibility = View.VISIBLE
            is LibraryState.Error -> Toast.makeText(requireContext(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show()
            is LibraryState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        binding.tvLibraryEmpty.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.rvItems.visibility = View.GONE
    }

    private fun showLibaryContent(bookList: List<Book>) {
        binding.progressBar.visibility = View.GONE
        binding.rvItems.visibility = View.VISIBLE
        bookListAdapter.removeItems()
        bookListAdapter.bookList.addAll(bookList)
        bookListAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}