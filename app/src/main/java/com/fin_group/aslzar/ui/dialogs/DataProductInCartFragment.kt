package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentDataProductInCartBinding

class DataProductInCartFragment : Fragment() {

    private var _binding: FragmentDataProductInCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataProductInCartBinding.inflate(inflater, container, false)
        return binding.root
    }
}