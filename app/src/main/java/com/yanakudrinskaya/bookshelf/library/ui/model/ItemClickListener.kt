package com.yanakudrinskaya.bookshelf.library.ui.model

import com.yanakudrinskaya.bookshelf.library.domain.models.Book


interface ItemClickListener {
    var onItemClick: ((Book) -> Unit)?
}