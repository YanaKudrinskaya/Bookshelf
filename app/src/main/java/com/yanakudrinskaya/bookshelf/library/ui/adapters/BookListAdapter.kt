package com.yanakudrinskaya.bookshelf.library.ui.adapters

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
import com.yanakudrinskaya.bookshelf.library.domain.models.Book
import com.yanakudrinskaya.bookshelf.library.ui.model.ItemClickListener
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

    fun removeItems() {
        bookList.clear()
    }
}

class BookListViewHolder(parent: ViewGroup) :  RecyclerView.ViewHolder (
    LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
) {
    private val bookImage: ImageView = itemView.findViewById(R.id.ivBookImage) //
    private val bookTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
    private val author: TextView = itemView.findViewById(R.id.tvAuthor)
    private val readStatus: ImageView = itemView.findViewById(R.id.ivReadStatusIcon) //кнопка при нажатии меняет статус на прочитанный

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
