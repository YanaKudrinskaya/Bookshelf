package com.yanakudrinskaya.bookshelf.on_boarding.ui.model

sealed interface BoardingNavigation {
    data class Content (val content: String) : BoardingNavigation
    data object Close : BoardingNavigation
}



