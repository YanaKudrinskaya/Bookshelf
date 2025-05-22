package com.yanakudrinskaya.bookshelf.login

import android.app.AlertDialog
import android.content.Context

object DialogUtils {
    fun showInfoAlert(
        context: Context,
        title: String = "Ошибка авторизации",
        message: String,
        positiveButtonText: String = "Далее",
        onPositiveClick: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
            .create()
            .show()
    }
}