package com.yanakudrinskaya.bookshelf.library.ui.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.ItemBookBinding
import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.ui.model.ItemClickListener
import java.io.File

class BookListAdapter : RecyclerView.Adapter<BookListViewHolder>(), ItemClickListener {

    var bookList = mutableListOf<Book>()

    override var onItemClick: ((Book) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        holder.bind(bookList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(bookList[position])
        }
    }

    fun removeItems() {
        bookList.clear()
    }
}

class BookListViewHolder(private val binding: ItemBookBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val context: Context = binding.root.context

    fun bind(item: Book) {
        Glide.with(itemView)
            .load(File(item.filePath))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.ivCover)
        binding.tvTitle.text = item.title
        val authorsText = if (item.authors.isNotEmpty()) {
            item.authors.joinToString(", ") { it.getFullName() }
        } else {
            ""
        }
        binding.tvAuthor.text = authorsText
        binding.ivReadStatus.imageTintList = if (item.readStatus) ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                R.color.brown
            )
        )
        else ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray))
    }
}
