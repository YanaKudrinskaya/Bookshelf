package com.yanakudrinskaya.bookshelf.main.ui.models

import com.yanakudrinskaya.bookshelf.main.domain.models.Book

interface ItemClickListener {
    var onItemClick: ((Book) -> Unit)?
}