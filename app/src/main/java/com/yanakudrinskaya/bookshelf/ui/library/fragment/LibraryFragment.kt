package com.yanakudrinskaya.bookshelf.ui.library.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentLibraryBinding
import com.yanakudrinskaya.bookshelf.domain.library.models.Book
import com.yanakudrinskaya.bookshelf.ui.library.adapters.BookListAdapter
import com.yanakudrinskaya.bookshelf.ui.library.model.LibraryState
import com.yanakudrinskaya.bookshelf.ui.library.view_model.LibraryViewModel
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

            }

            override fun afterTextChanged(s: Editable?) {
                searchString = binding.etSearch.text.toString()

            }
        }

        simpleTextWatcher.let { binding.etSearch.addTextChangedListener(it) }

        binding.etSearch.setOnFocusChangeListener { view, hasFocus ->
            binding.fabAddBook.isVisible = !hasFocus
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
            is LibraryState.Empty -> {
                showLoading(false)
                binding.tvLibraryEmpty.visibility = View.VISIBLE
            }
            is LibraryState.Error -> {
                showError()
            }
            is LibraryState.Loading -> showLoading(true)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.tvLibraryEmpty.isVisible = !show
        binding.progressBar.isVisible = show
        binding.rvItems.isVisible = !show
    }

    private fun  showError() {
        showLoading(false)
        Toast.makeText(requireContext(), "Не смог загрузить библиотеку", Toast.LENGTH_SHORT).show()
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
}