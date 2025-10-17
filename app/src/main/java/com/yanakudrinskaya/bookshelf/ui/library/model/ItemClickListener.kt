package com.yanakudrinskaya.bookshelf.ui.library.model

import com.yanakudrinskaya.bookshelf.domain.library.models.Book


interface ItemClickListener {
    var onItemClick: ((Book) -> Unit)?
}