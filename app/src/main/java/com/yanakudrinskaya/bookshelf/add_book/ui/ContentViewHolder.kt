package com.yanakudrinskaya.bookshelf.add_book.ui

import androidx.recyclerview.widget.RecyclerView
import com.yanakudrinskaya.bookshelf.databinding.ItemContentBinding
import com.yanakudrinskaya.bookshelf.library.domain.models.Work

class ContentViewHolder (private val binding: ItemContentBinding) : RecyclerView.ViewHolder(binding.root)  {

    fun bind(item: Work) {
        // Формируем строку со всеми авторами
        val authorsText = if (item.authors.isNotEmpty()) {
            item.authors.joinToString(", ") { it.getFullName() }
        } else {
            ""
        }
        binding.tvContentAuthor.text = authorsText
        binding.tvContentTitle.text = item.title
    }
}