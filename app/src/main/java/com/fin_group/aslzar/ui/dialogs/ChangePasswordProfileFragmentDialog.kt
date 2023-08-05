package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentDialogChangePasswordProfileBinding
import com.fin_group.aslzar.databinding.FragmentProfileBinding
import com.fin_group.aslzar.util.BaseDialogFragment


class ChangePasswordProfileFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentDialogChangePasswordProfileBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDialogChangePasswordProfileBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView2.setOnClickListener {
            dismiss()
        }
    }

}