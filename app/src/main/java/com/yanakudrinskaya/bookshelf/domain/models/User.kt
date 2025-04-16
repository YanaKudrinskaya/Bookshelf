package com.yanakudrinskaya.bookshelf.domain.models

import android.graphics.Bitmap

data class User(
    var name: String,
    var email: String,
    var avatar: Bitmap? = null
)

