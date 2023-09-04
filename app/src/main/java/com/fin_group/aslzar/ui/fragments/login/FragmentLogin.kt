package com.fin_group.aslzar.ui.fragments.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.fin_group.aslzar.util.SessionManager
import com.google.android.material.textfield.TextInputEditText
import java.security.SecureRandom
import java.security.spec.KeySpec
import android.util.Base64
import android.view.View.VISIBLE
import android.widget.ProgressBar
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.response.Auth
import com.fin_group.aslzar.util.FunCallback
import com.fin_group.aslzar.util.SessionManager.Companion.IS_LOGGED_IN_KEY
import com.fin_group.aslzar.util.SessionManager.Companion.PREFS_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    lateinit var progressBar: ProgressBar

    private lateinit var keystoreManager: KeystoreManager
    private lateinit var encryptionManager: EncryptionManager

    private lateinit var sessionManager: SessionManager

    lateinit var api: ApiClient
    lateinit var sharedPreferences: SharedPreferences

    private var isLoggedIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        sessionManager = SessionManager(requireContext())
        api = ApiClient
        api.init(sessionManager, binding.root)

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)

        loginEt = binding.editLogin
        passwordEt = binding.editPassword
        progressBar = binding.progressBar2


        binding.btnLogin.setOnClickListener {
            val login = loginEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (login.isEmpty()) {
                loginEt.error = "Введите имя пользователя"
                loginEt.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordEt.error = "Введите пароль"
                passwordEt.requestFocus()
                return@setOnClickListener
            }

            progressBar.visibility = VISIBLE

            val call = api.getApiServiceLogin(login, password).userLogin()
            call.enqueue(object : Callback<Auth?> {
                override fun onResponse(call: Call<Auth?>, response: Response<Auth?>) {
                    try {
                        if (response.isSuccessful){
                            val loginResponse = response.body()
                            if (loginResponse?.result != null){
                                val secureRandom = SecureRandom()
                                val keyBytes = ByteArray(32)
                                secureRandom.nextBytes(keyBytes)
                                val encryptionKey = SecretKeySpec(keyBytes, "AES")
                                val savingKey = Base64.encodeToString(encryptionKey.encoded, Base64.DEFAULT)

                                encryptionManager = EncryptionManager(encryptionKey)

                                val encryptedLogin = encryptionManager.encryptData(login)
                                val encryptedPassword = encryptionManager.encryptData(password)

                                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                editor.putBoolean(IS_LOGGED_IN_KEY, true)
                                editor.apply()

                                sessionManager.saveLogin(encryptedLogin)
                                sessionManager.savePassword(encryptedPassword)
                                sessionManager.saveKey(savingKey)
                                sessionManager.saveToken(loginResponse.access_token)
                                sessionManager.saveUserLocation(loginResponse.result.location)
                                sessionManager.saveName(loginResponse.result.fio)
                                progressBar.visibility = VISIBLE

                                Toast.makeText(requireContext(), "Добро пожаловать ${loginResponse.result.fio}!", Toast.LENGTH_SHORT).show()

                                val i = Intent(requireContext(), MainActivity::class.java)
                                startActivity(i)
                                requireActivity().finish()
                            }
                        } else {
                            if (response.code() == 401) {
                                binding.progressBar2.visibility = View.GONE
                                Toast.makeText(requireContext(),"Логин или пароль введены неправильно!",Toast.LENGTH_SHORT).show()
                                api.clearPassLogin()
                                loginEt.setText("")
                                passwordEt.setText("")
                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), "Ошибка при входе", Toast.LENGTH_SHORT).show()
                                api.clearPassLogin()
                            }
                            if (sessionManager.isLoginAndPasswordSaved()) {
                                api.clearPassLogin()
                            }
                            progressBar.visibility = View.GONE
                        }
                    }catch (e: Exception){
                        progressBar.visibility = View.GONE
                        e.printStackTrace()
                        Log.d("TAG", "onResponse: ${e.message}")
                    }
                }

                override fun onFailure(call: Call<Auth?>, t: Throwable) {
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
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

    override fun onStart() {
        super.onStart()
        if (isLoggedIn) {
            val i = Intent(requireActivity(), MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}