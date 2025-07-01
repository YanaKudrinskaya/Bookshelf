package com.yanakudrinskaya.bookshelf.add_book.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.add_book.ui.ContentAdapter
import com.yanakudrinskaya.bookshelf.add_book.ui.models.BookState
import com.yanakudrinskaya.bookshelf.add_book.ui.models.DialogEvent
import com.yanakudrinskaya.bookshelf.add_book.ui.view_model.AddBookViewModel
import com.yanakudrinskaya.bookshelf.databinding.FragmentAddBookBinding
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
        viewModel.getContentLiveData().observe(viewLifecycleOwner) {
            updateContentList(it)
        }

        viewModel.getDialogLiveData().observe(viewLifecycleOwner) {
            when(it) {
                DialogEvent.CONTENT -> showAddContentDialog()
                DialogEvent.AUTHOR -> showAuthorDialog()
                DialogEvent.TOAST -> showError("Заполните все поля книги")
            }
        }

        viewModel.getAddBookLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is BookState.Error -> showError(state.e)
                BookState.Loading -> showLoading()
                BookState.Success -> showCloseMessage()
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

        binding.tvAddWorkBtn.setOnClickListener {
            showAddContentDialog()
        }

        binding.tvSaveBook.setOnClickListener {
            saveBook()
        }

        binding.etYear.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val input = p0.toString()
                if (input.length == 4) {
                    val year = input.toIntOrNull()
                    if (year == null || year < 1900 || year > 2100) {
                        binding.etYear.error = "Введите корректный год (1900-2100)"
                    } else {
                        binding.etYear.error = null
                    }
                }
            }

        })
    }

    private fun showAddContentDialog() {
        val bookAuthor = binding.etAuthor.text.toString()
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_work, null)
        val etAuthor = dialogView.findViewById<EditText>(R.id.etWorkAuthor)
        val etTitle = dialogView.findViewById<EditText>(R.id.etWorkTitle)

        if (!isAnthology && bookAuthor.isNotEmpty()) {
            etAuthor.setText(bookAuthor)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить произведение")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val workAuthor = etAuthor.text.toString()
                val workTitle = etTitle.text.toString()

                if (workAuthor.isNotEmpty() && workTitle.isNotEmpty()) {
                    viewModel.saveWork(Work(workAuthor, workTitle))
                } else {
                    Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAuthorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Автор не указан")
            .setMessage("Книга является сборником произведений нескольких авторов?")
            .setPositiveButton("Да") { _, _ ->
                isAnthology = true
                binding.etAuthor.setText("Сборник")
            }
            .setNegativeButton("Нет") { _, _ ->
                isAnthology = false
                binding.etAuthor.requestFocus()
                Toast.makeText(requireContext(), "Укажите автора книги", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
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
        val year = binding.etYear.text.toString()

        viewModel.saveBook(author, title, publisher, year)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}