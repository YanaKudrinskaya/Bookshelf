package com.yanakudrinskaya.bookshelf.profile.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.databinding.DialogChangeNameBinding


class ChangeNameBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: DialogChangeNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChangeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etName.setText(arguments?.getString("currentName"))

        binding.etName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveName()
                dismiss()
                true
            } else {
                false
            }
        }

        binding.ibCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun saveName() {
        val newName = binding.etName.text.toString()
        parentFragmentManager.setFragmentResult("name_change", bundleOf("newName" to newName))
        dismiss()
    }

}
