package com.fin_group.aslzar.ui.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fin_group.aslzar.databinding.FragmentSignOutProfileDialogBinding
import com.fin_group.aslzar.ui.activities.LoginActivity
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.setWidthPercent

class SignOutProfileFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentSignOutProfileDialogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignOutProfileDialogBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(90)

        binding.apply {
            actionCloseProfile.setOnClickListener { dismiss() }
            actionYesBtn.setOnClickListener {
                val i = Intent(context, LoginActivity::class.java)
                startActivity(i)
                activity?.finish()
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