package com.fin_group.aslzar.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.fin_group.aslzar.databinding.FragmentLoginBinding
import com.fin_group.aslzar.ui.activities.MainActivity

class FragmentLogin : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var doubleBackToExitPressedOnce = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            val login = "123456789"
            val password = "987654321"
            val logIn = binding.login
            val passWord = binding.password
            val loginText = logIn.editText?.text.toString()
            val passwordText = passWord.editText?.text.toString()

            if (loginText == login && passwordText == password) {
                val i = Intent(requireActivity(), MainActivity::class.java)
                startActivity(i)
                requireActivity().finish()
            } else if (loginText.isEmpty()) {
                binding.login.error = "Введите логин"
                binding.login.requestFocus()
                return@setOnClickListener
            } else if (passwordText.isEmpty()) {
                binding.password.error = "Введите пароль"
                binding.password.requestFocus()
                return@setOnClickListener
            } else if (loginText != login) {
                binding.login.error = "Неверный логин"
                binding.login.requestFocus()
                return@setOnClickListener
            } else {
                binding.password.error = "Неверный пароль"
                binding.password.requestFocus()
                return@setOnClickListener
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (doubleBackToExitPressedOnce) {
                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Нажмите еще раз \"Назад\" для выхода",
                            Toast.LENGTH_SHORT
                        ).show()
                        doubleBackToExitPressedOnce = true

                        Handler(Looper.getMainLooper()).postDelayed({
                            doubleBackToExitPressedOnce = false
                        }, 2000)
                    }
                }
            })

        return binding.root
    }
}