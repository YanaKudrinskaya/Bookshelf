package com.yanakudrinskaya.bookshelf

import android.graphics.Bitmap

object User {
    var userName: String? = null
    //var userEmail: String? = null
    var avatarBitmap: Bitmap? = null
    fun clear() {
        userName = null
        avatarBitmap = null
    }
}