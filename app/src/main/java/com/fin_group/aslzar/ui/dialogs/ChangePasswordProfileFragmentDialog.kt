package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fin_group.aslzar.databinding.FragmentDialogChangePasswordProfileBinding
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.hideBottomNav
import com.fin_group.aslzar.util.setWidthPercent
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordProfileFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentDialogChangePasswordProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var newPasswordInputLayout: TextInputLayout
    private lateinit var newPasswordEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogChangePasswordProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(90)

        newPasswordInputLayout = binding.newPasswordChange
        newPasswordEditText = binding.editNewPasswordChange
        passwordCheck()

        binding.floatingActionButton.setOnClickListener {
            dismiss()
        }
    }

    private fun passwordCheck() {

        val oldPassword = binding.oldPasswordChange
        val newPassword = binding.newPasswordChange
        val repeatPassword = binding.repeatNewPasswordChange
        val saveButton = binding.btnSaveChangeData

        saveButton.setOnClickListener {
            val oldPasswordText = oldPassword.editText?.text.toString()
            val newPasswordText = newPassword.editText?.text.toString()
            val repeatPasswordText = repeatPassword.editText?.text.toString()

            if (oldPasswordText.isEmpty()) {
                binding.editOldPasswordChange.error = "Введите старый пароль!"
                binding.editOldPasswordChange.requestFocus()
                return@setOnClickListener
            } else if (newPasswordText.isEmpty()) {
                binding.editNewPasswordChange.error = "Введите новый пароль!"
                binding.editNewPasswordChange.requestFocus()
                return@setOnClickListener
            } else if (newPasswordText.length < 8) {
                binding.editNewPasswordChange.error = "Минимальная длина паролья 8 символов"
                binding.editNewPasswordChange.requestFocus()
                return@setOnClickListener
            } else if (repeatPasswordText.isEmpty()) {
                binding.editRepeatNewPasswordChange.error = "Повторите новый пароль!"
                binding.editRepeatNewPasswordChange.requestFocus()
                return@setOnClickListener
            }
            if (newPasswordText == repeatPasswordText) {
                dismiss()
                Toast.makeText(requireContext(), "Пароль успешно изменён!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }



    override fun onStart() {
        super.onStart()
    }
}