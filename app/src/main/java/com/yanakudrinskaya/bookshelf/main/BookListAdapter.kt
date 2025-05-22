package com.yanakudrinskaya.bookshelf.main

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.main.domain.models.Book
import com.yanakudrinskaya.bookshelf.main.ui.models.ItemClickListener
import java.io.File

class BookListAdapter : RecyclerView.Adapter<BookListViewHolder> (), ItemClickListener {
    var bookList = mutableListOf<Book>()
    override var onItemClick: ((Book) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder = BookListViewHolder(parent)

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        holder.bind(bookList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(bookList[position])
        }
    }
}

class BookListViewHolder(parent: ViewGroup) :  RecyclerView.ViewHolder (
    LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
) {
    private val bookImage: ImageView = itemView.findViewById(R.id.bookImage) //
    private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
    private val author: TextView = itemView.findViewById(R.id.author)
    private val readStatus: ImageView = itemView.findViewById(R.id.readStatus) //кнопка при нажатии меняет статус на провитанный

    private val context: Context = parent.context

    fun bind(item: Book) {
        Glide.with(itemView)
            .load(File(item.filePath))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(bookImage)
        bookTitle.text = item.title
        author.text = item.author
        readStatus.imageTintList = if (item.readStatus) ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brown_2)) else ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray))
    }

}
