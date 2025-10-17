package com.yanakudrinskaya.bookshelf.ui.on_boarding.model

sealed interface BoardingNavigation {
    data class Content (val content: String) : BoardingNavigation
    data object Close : BoardingNavigation
}



