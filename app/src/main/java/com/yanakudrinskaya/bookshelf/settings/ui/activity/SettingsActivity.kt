package com.yanakudrinskaya.bookshelf.settings.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.yanakudrinskaya.bookshelf.databinding.ActivitySettingsBinding
import com.yanakudrinskaya.bookshelf.login.ui.activity.LoginActivity
import com.yanakudrinskaya.bookshelf.settings.ui.model.ImagePickEvent
import com.yanakudrinskaya.bookshelf.settings.ui.model.PermissionState
import com.yanakudrinskaya.bookshelf.settings.ui.model.UserState
import com.yanakudrinskaya.bookshelf.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                viewModel.processSelectedImage(it, fromCamera = false)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                viewModel.processSelectedImage(null, fromCamera = true)
            }
        }

    private var name: String = ""
    private var email: String = ""

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
        setupObservers()
        setupClickListeners()
        setupData()
    }

    private fun setupData() {
        viewModel.checkPermissions()
        viewModel.loadUserProfile()
        viewModel.loadAvatar()
    }

    private fun setupObservers() {
        viewModel.getUserLiveData().observe(this) { data ->
            if (data.name != name || data.email != email) saveData(data)
            setupAvatar(data)
        }

        viewModel.getImagePickEvent().observe(this) { event ->
            when (event) {
                is ImagePickEvent.FromGallery -> pickImageLauncher.launch(event.request)
                is ImagePickEvent.FromCamera -> takePictureLauncher.launch(event.uri)
                is ImagePickEvent.Default -> viewModel.deleteAvatar()
                is ImagePickEvent.Error -> showError(event.message)
            }
        }

        viewModel.getPermissionState().observe(this) { state ->
            when (state) {
                is PermissionState.Denied -> {
                    showError("Some features may not work without permissions: ${state.permissions.joinToString()}")
                }

                is PermissionState.Error -> {
                    showError(state.message)
                }

                PermissionState.Granted -> {
                }
            }
        }
    }


    private fun setupClickListeners() {
        binding.avatar.setOnClickListener {
            showImagePickDialog()
        }
        binding.libraryBtn.setOnClickListener { finish() }
        binding.logoutBtn.setOnClickListener {
            viewModel.triggerLogout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupViews() {
        Log.d("Myregister", "Установила данные пользователя")
        binding.userNameText.text = name
        binding.settingsLabel.text = name
        binding.email.text = email
    }

    private fun saveData(data: UserState) {
        name = data.name
        email = data.email
        setupViews()
    }

    private fun setupAvatar(data: UserState) {
        Log.d("Myregister", "Установила аватар")
        if(data.filePath != null) {
            Glide.with(this@SettingsActivity)
                .load(File(data.filePath))
                .signature(ObjectKey(System.currentTimeMillis()))
                .circleCrop()
                .into(binding.avatar)
        } else if(data.placeholder != null) {
            binding.avatar.setImageResource(data.placeholder)
        }

    }

    private fun showImagePickDialog() {
        val options = arrayOf("Из галереи", "Сделать фото", "По умолчанию", "Отмена")
        AlertDialog.Builder(this)
            .setTitle("Выберите источник")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.onGallerySelected()
                    1 -> viewModel.onCameraSelected()
                    2 -> viewModel.onDefaultSelected()
                }
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.handlePermissionsResult(requestCode, permissions, grantResults)
    }

    fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0);
    }
}