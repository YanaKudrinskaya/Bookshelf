package com.yanakudrinskaya.bookshelf.ui.wish.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yanakudrinskaya.bookshelf.databinding.FragmentWishBinding
import com.yanakudrinskaya.bookshelf.ui.wish.view_model.WishViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class WishFragment : Fragment() {

    private val viewModel by viewModel<WishViewModel>()

    private var _binding: FragmentWishBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentWishBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        fun newInstance() = WishFragment().apply {
            arguments = Bundle().apply {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
