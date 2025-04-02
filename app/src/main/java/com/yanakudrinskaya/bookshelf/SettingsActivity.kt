package com.yanakudrinskaya.bookshelf

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import android.Manifest
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.yanakudrinskaya.bookshelf.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val sharedViewModel by lazy {
        (application as MyApp).sharedViewModel
    }
    private val app by lazy { application as MyApp }

    private lateinit var currentPhotoPath: String
    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2
    private val PERMISSIONS_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        setupViews()
        checkPermissions()

    }

    private fun setupViews() {
        binding.userNameText.text = User.userName

        // Загрузка аватарки через AvatarManager
        MyApp.AvatarManager.loadAvatar(this)?.let {
            binding.avatar.setImageBitmap(it)
            User.avatarBitmap = it
        }

        binding.avatar.setOnClickListener { showImagePickDialog() }
        binding.libraryBtn.setOnClickListener { finish() }
        binding.logoutBtn.setOnClickListener {
            sharedViewModel.triggerLogout()
            finish()
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>().apply {
            add(Manifest.permission.CAMERA)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ (API 33+)
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                // Android 6-12 (API 23-32)
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

        if (permissions.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Некоторые функции недоступны", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImagePickDialog() {
        val options = arrayOf("Из галереи", "Сделать фото", "Отмена")
        AlertDialog.Builder(this)
            .setTitle("Выберите источник")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickFromGallery()
                    1 -> takePhoto()
                }
            }
            .show()
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                try {
                    val photoFile = createImageFile()
                    val photoUri = FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        photoFile
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(intent, CAMERA_REQUEST)
                } catch (e: IOException) {
                    Toast.makeText(this, "Ошибка создания файла", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        return File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ).apply { currentPhotoPath = absolutePath }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return

        when (requestCode) {
            PICK_IMAGE_REQUEST -> data?.data?.let { processSelectedImage(it, false) }
            CAMERA_REQUEST -> processSelectedImage(null, true)
        }
    }

    private fun processSelectedImage(uri: Uri?, fromCamera: Boolean) {
        try {
            val bitmap = if (fromCamera) {
                BitmapFactory.decodeFile(currentPhotoPath)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }

            val path = if (fromCamera) currentPhotoPath else getPathFromUri(uri!!)
            val finalBitmap = resizeBitmap(handleImageRotation(bitmap, path))

            // Сохранение через AvatarManager
            if (MyApp.AvatarManager.saveAvatar(this, finalBitmap)) {
                binding.avatar.setImageBitmap(finalBitmap)
                User.avatarBitmap = finalBitmap
            } else {
                Toast.makeText(this, "Ошибка сохранения аватарки", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        return contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)?.use {
            if (it.moveToFirst()) it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)) else null
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int = 800): Bitmap {
        val ratio = bitmap.width.toFloat() / bitmap.height
        val (width, height) = if (ratio > 1) maxSize to (maxSize / ratio).toInt()
        else (maxSize * ratio).toInt() to maxSize
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun handleImageRotation(bitmap: Bitmap, path: String?): Bitmap {
        val orientation = path?.let {
            try {
                ExifInterface(path).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } catch (e: IOException) {
                ExifInterface.ORIENTATION_NORMAL
            }
        } ?: ExifInterface.ORIENTATION_NORMAL

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height,
            Matrix().apply { postRotate(degrees) }, true
        )
    }
}