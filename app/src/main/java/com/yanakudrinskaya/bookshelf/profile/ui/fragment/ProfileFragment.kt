package com.yanakudrinskaya.bookshelf.profile.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.FragmentProfileBinding
import com.yanakudrinskaya.bookshelf.profile.ui.model.ImagePickEvent
import com.yanakudrinskaya.bookshelf.profile.ui.model.PermissionState
import com.yanakudrinskaya.bookshelf.profile.ui.model.UserState
import com.yanakudrinskaya.bookshelf.profile.ui.view_model.ProfileViewModel

import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class ProfileFragment : Fragment() {

    private val viewModel by viewModel<ProfileViewModel>()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        parentFragmentManager.setFragmentResultListener(CHANGE_NAME, this) { _, bundle ->
            val newName = bundle.getString(NEW_NAME) ?: return@setFragmentResultListener
            viewModel.changeName(newName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.getUserLiveData().observe(viewLifecycleOwner) { data ->
            if (data.name != name || data.email != email) saveData(data)
            if (data.avatarIsChange) setupAvatar(data)
        }

        viewModel.getImagePickEvent().observe(viewLifecycleOwner) { event ->
            when (event) {
                is ImagePickEvent.FromGallery -> pickImageLauncher.launch(event.request)
                is ImagePickEvent.FromCamera -> takePictureLauncher.launch(event.uri)
                is ImagePickEvent.Default -> viewModel.deleteAvatar()
                is ImagePickEvent.Error -> showError(event.message)
            }
        }

        viewModel.getPermissionState().observe(viewLifecycleOwner) { state ->
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

        binding.ivAvatar.setOnClickListener {
            showImagePickDialog()
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_auth_graph)
        }
        binding.tvUserName.setOnClickListener {
            showNameChangeBottomSheet()
        }
    }

    private fun showNameChangeBottomSheet() {
        val dialog = ChangeNameBottomSheet().apply {
            arguments = bundleOf(CURRENT_NAME to name)
        }
        dialog.show(parentFragmentManager, CHANGE_NAME_BOTTOM)
    }


    private fun setupViews() {
        binding.tvUserName.text = name
        binding.tvEmail.text = email
    }

    private fun saveData(data: UserState) {
        name = data.name
        email = data.email
        setupViews()
    }

    private fun setupAvatar(data: UserState) {
        if (data.filePath != null) {
            Glide.with(requireContext())
                .load(File(data.filePath))
                .signature(ObjectKey(System.currentTimeMillis()))
                .circleCrop()
                .into(binding.ivAvatar)
        } else if (data.placeholder != null) {
            binding.ivAvatar.setImageResource(data.placeholder)
        }
    }

    private fun showImagePickDialog() {
        val options = arrayOf(
            requireContext().getString(R.string.from_gallery),
            requireContext().getString(R.string.take_photo),
            requireContext().getString(R.string.by_default),
            requireContext().getString(R.string.cancel)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.select))
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CURRENT_NAME = "currentName"
        private const val CHANGE_NAME_BOTTOM = "ChangeNameBottomSheet"
        private const val NEW_NAME = "newName"
        private const val CHANGE_NAME = "name_change"
    }
}