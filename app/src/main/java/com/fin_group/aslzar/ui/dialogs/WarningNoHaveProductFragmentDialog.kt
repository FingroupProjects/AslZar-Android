package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentDialogWarningNoHaveProductBinding
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.setWidthPercent


class WarningNoHaveProductFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentDialogWarningNoHaveProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogWarningNoHaveProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(80)

        binding.apply {
            actionClose.setOnClickListener { dismiss() }
            actionYes.setOnClickListener {
                Toast.makeText(requireContext(), "Запрос на получение товара отправлен", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            actionNo.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}