package com.fin_group.aslzar.ui.dialogs

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentDialogChangePasswordProfileBinding
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.hideBottomNav
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nulabinc.zxcvbn.Zxcvbn

class ChangePasswordProfileFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentDialogChangePasswordProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressBar: ProgressBar
    private lateinit var newPasswordInputLayout: TextInputLayout
    private lateinit var newPasswordEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogChangePasswordProfileBinding.inflate(inflater, container, false)

        newPasswordInputLayout = binding.newPasswordChange
        newPasswordEditText = binding.editNewPasswordChange

        //passwordCheck()

//        newPasswordEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                val newPassword = s?.toString() ?: ""
//                updateProgressBar(newPassword)
//            }
//        })

        return binding.root
    }

    private fun updateProgressBar(password: String) {
        val zxcvbn = Zxcvbn()
        val result = zxcvbn.measure(password)
        val score = result.score
        progressBar.progress = score

        when (score) {
            0 -> progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.progressColorVeryWeak))
            1 -> progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.progressColorWeak))
            2 -> progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.progressColorMedium))
            3 -> progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.progressColorStrong))
            4 -> progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.progressColorVeryStrong))
        }
    }




    private fun passwordCheck(){

        val oldPassword = binding.oldPasswordChange
        val newPassword = binding.newPasswordChange
        val repeatPassword = binding.repeatNewPasswordChange
        val saveButton = binding.btnSaveChangeData
        saveButton.setOnClickListener {
            val oldPasswordText = oldPassword.editText?.text.toString()
            val newPasswordText = newPassword.editText?.text.toString()
            val repeatPasswordText = repeatPassword.editText?.text.toString()

            if (oldPasswordText.isEmpty()){
                binding.editOldPasswordChange.error = "Введите старый пароль!"
                binding.editOldPasswordChange.requestFocus()
                return@setOnClickListener
            }
            else if (newPasswordText.isEmpty()){
                binding.editNewPasswordChange.error = "Введите новый пароль!"
                binding.editNewPasswordChange.requestFocus()
                return@setOnClickListener
            }
            else if (newPasswordText.length < 8){
                binding.editNewPasswordChange.error = "Минимальная длина паролья 8 символов"
                binding.editNewPasswordChange.requestFocus()
                return@setOnClickListener
            }
            else if (repeatPasswordText.isEmpty()){
                binding.editRepeatNewPasswordChange.error = "Повторите новый пароль!"
                binding.editRepeatNewPasswordChange.requestFocus()
                return@setOnClickListener
            }
            if (newPasswordText == repeatPasswordText){
                dismiss()
                Toast.makeText(requireContext(), "Пароль успешно изменён!", Toast.LENGTH_SHORT).show()

            }else{
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }
}