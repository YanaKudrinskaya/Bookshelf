package com.yanakudrinskaya.bookshelf

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileOutputStream

class MyApp : Application() {

    companion object {
        const val APP_PREFERENCES = "app_prefs"
        const val IS_FIRST_LAUNCH = "is_first_launch"
    }

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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
        val auth = Firebase.auth
        val db = Firebase.firestore
    }
}