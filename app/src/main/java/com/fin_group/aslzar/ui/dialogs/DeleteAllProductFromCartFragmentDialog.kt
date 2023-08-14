package com.fin_group.aslzar.ui.dialogs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentDeleteAllProductFromCartDialogBinding
import com.fin_group.aslzar.databinding.FragmentSignOutProfileDialogBinding
import com.fin_group.aslzar.ui.activities.LoginActivity
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.setWidthPercent

class DeleteAllProductFromCartFragmentDialog : BaseDialogFragment() {

    private var _binding : FragmentDeleteAllProductFromCartDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeleteAllProductFromCartDialogBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(90)

        binding.apply {
            actionClose.setOnClickListener { dismiss() }
            actionYesBtn.setOnClickListener {
                Toast.makeText(requireContext(), "Ваша корзина успешно очищена!", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            actionNoBtn.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}