package com.yanakudrinskaya.bookshelf.presentation.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.yanakudrinskaya.bookshelf.App
import com.yanakudrinskaya.bookshelf.Creator
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.ActivitySettingsBinding
import com.yanakudrinskaya.bookshelf.domain.impl.AvatarUseCase
import com.yanakudrinskaya.bookshelf.domain.interactors.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.domain.models.UserCurrent
import kotlinx.coroutines.launch
import java.util.Currency

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var avatarUseCase: AvatarUseCase

    private val sharedViewModel by lazy {
        (application as App).sharedViewModel
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { processSelectedImage(it, fromCamera = false) }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            processSelectedImage(null, fromCamera = true)
        }
    }

    private lateinit var currentPhotoPath: String
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
        avatarUseCase = Creator.provideAvatarUseCase()

        checkPermissions()
        setupViews()
    }

    private fun setupViews() {
        binding.userNameText.text = UserCurrent.name
        binding.settingsLabel.text = UserCurrent.name
        binding.email.text = UserCurrent.email
        loadAvatar()
        binding.avatar.setOnClickListener { showImagePickDialog() }
        binding.libraryBtn.setOnClickListener { finish() }
        binding.logoutBtn.setOnClickListener {
            sharedViewModel.triggerLogout()
            finish()
        }
    }

    /////////////////// Avatar
    private fun loadAvatar() {
        lifecycleScope.launch {
            avatarUseCase.loadAvatar(UserCurrent.id)?.let { avatar ->
                Glide.with(this@SettingsActivity)
                    .load(File(avatar.filePath))
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .circleCrop()
                    .into(binding.avatar)
            } ?: run {
                binding.avatar.setImageResource(R.drawable.placeholder)
            }
        }
    }

    private fun showImagePickDialog() {
        val options = arrayOf("Из галереи", "Сделать фото", "По умолчанию", "Отмена")
        AlertDialog.Builder(this)
            .setTitle("Выберите источник")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickFromGallery()
                    1 -> takePhoto()
                    2 -> deleteAvatar()
                }
            }
            .show()
    }

    private fun pickFromGallery() {
        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun takePhoto() {
        try {
            val photoFile = createImageFile()
            currentPhotoPath = photoFile.absolutePath
            val photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(photoUri)
        } catch (e: IOException) {
            Toast.makeText(this, "Ошибка создания файла", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteAvatar() {
        avatarUseCase.deleteAvatar(UserCurrent.id)
        Glide.with(this@SettingsActivity)
            .load(R.drawable.placeholder)
            .circleCrop()
            .into(binding.avatar)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        return File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ).apply { currentPhotoPath = absolutePath }
    }

    private fun processSelectedImage(uri: Uri?, fromCamera: Boolean) {
        lifecycleScope.launch {
            val success = if (fromCamera) {
                avatarUseCase.saveAvatarFromCamera(UserCurrent.id, currentPhotoPath)
            } else {
                uri?.let { avatarUseCase.saveAvatarFromUri(UserCurrent.id, it) } ?: false
            }

            if (success) {
                loadAvatar()
            } else {
                Toast.makeText(this@SettingsActivity, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /////////////////// Permissions
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
                //Toast.makeText(this, "Некоторые функции недоступны", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0);
    }
}