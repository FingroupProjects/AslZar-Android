package com.fin_group.aslzar.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.fin_group.aslzar.cipher.EncryptionManager
import com.fin_group.aslzar.cipher.KeystoreManager
import com.fin_group.aslzar.databinding.FragmentLoginBinding
import com.fin_group.aslzar.ui.activities.MainActivity
import com.google.android.material.textfield.TextInputEditText
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class FragmentLogin : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var doubleBackToExitPressedOnce = false

    lateinit var loginEt: TextInputEditText
    lateinit var passwordEt: TextInputEditText

    private lateinit var keystoreManager: KeystoreManager
    private lateinit var encryptionManager: EncryptionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        loginEt = binding.editLogin
        passwordEt = binding.editPassword

        binding.btnLogin.setOnClickListener {
            val login = loginEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

//            if (login.isEmpty()) {
//                loginEt.error = "Введите имя пользователя"
//                loginEt.requestFocus()
//                return@setOnClickListener
//            }
//
//            if (password.isEmpty()) {
//                passwordEt.error = "Введите пароль"
//                passwordEt.requestFocus()
//                return@setOnClickListener
//            }
//
//            val secureRandom = SecureRandom()
//            val keyBytes = ByteArray(32) // 256 битовый ключ
//            secureRandom.nextBytes(keyBytes)
//
//            val encryptionKey = SecretKeySpec(keyBytes, "AES")
//
//            encryptionManager = EncryptionManager(encryptionKey)
//
//            val encryptedLogin = encryptionManager.encryptData(login)
//            val decryptedLogin = encryptionManager.decryptData(encryptedLogin)
//
//            val encryptedPassword = encryptionManager.encryptData(password)
//            val decryptedPassword = encryptionManager.decryptData(encryptedPassword)
//
//            Log.d("TAG", "onCreateView: Original Login: $login")
//            Log.d("TAG", "onCreateView: Encrypted Login: $encryptedLogin")
//            Log.d("TAG", "onCreateView: Decrypted Login: $decryptedLogin")
//
//            Log.d("TAG", "onCreateView: Original Password: $password")
//            Log.d("TAG", "onCreateView: Encrypted Password: $encryptedPassword")
//            Log.d("TAG", "onCreateView: Decrypted Password: $decryptedPassword")
//
//            Log.d("TAG", "onCreateView: key $encryptionKey")

            val i = Intent(requireActivity(), MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (doubleBackToExitPressedOnce) {
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(),"Нажмите еще раз \"Назад\" для выхода",Toast.LENGTH_SHORT).show()
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