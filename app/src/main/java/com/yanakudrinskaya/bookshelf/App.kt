package com.yanakudrinskaya.bookshelf

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileOutputStream

class App : Application() {

    val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(SharedViewModel::class.java)
    }

    // Менеджер аватарок
    object AvatarManager {
        private const val AVATAR_FILE = "avatar.jpg"

        fun saveAvatar(context: Context, bitmap: Bitmap): Boolean {
            return try {
                val file = File(context.filesDir, AVATAR_FILE)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                true
            } catch (e: Exception) {
                false
            }
        }

        fun loadAvatar(context: Context): Bitmap? {
            val file = File(context.filesDir, AVATAR_FILE)
            return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        }

        fun deleteAvatar(context: Context) {
            File(context.filesDir, AVATAR_FILE).delete()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
    }
}