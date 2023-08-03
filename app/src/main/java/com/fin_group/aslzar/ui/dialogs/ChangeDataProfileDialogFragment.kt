package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentDialogChangeDataProfileBinding

class ChangeDataProfileDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogChangeDataProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogChangeDataProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

}