package com.yanakudrinskaya.bookshelf.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun dateFormat(date: Date) : String {
    return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date)
}