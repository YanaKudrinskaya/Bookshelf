package com.yanakudrinskaya.bookshelf.add_book.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yanakudrinskaya.bookshelf.databinding.ItemContentBinding
import com.yanakudrinskaya.bookshelf.library.domain.models.Work
import com.yanakudrinskaya.bookshelf.library.ui.model.ItemClickListener

class ContentAdapter : RecyclerView.Adapter<ContentViewHolder> ()/*, ItemClickListener*/ {

    var contentList = listOf<Work>()
    //override var onItemClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return ContentViewHolder(ItemContentBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(contentList[position])
        /*holder.itemView.setOnClickListener {
            onItemClick?.invoke(trackList[position])
        }*/
    }

    /*fun removeItems() {
        content.clear()
    }*/
}