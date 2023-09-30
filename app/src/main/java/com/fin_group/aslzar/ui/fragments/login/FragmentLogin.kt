package com.fin_group.aslzar.ui.fragments.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.util.Base64
import android.view.View.VISIBLE
import android.widget.ProgressBar
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.response.Auth
import com.fin_group.aslzar.ui.fragments.forgotPassword.ForgotPasswordFragment
import com.fin_group.aslzar.util.SessionManager.Companion.IS_LOGGED_IN_KEY
import com.fin_group.aslzar.util.SessionManager.Companion.PREFS_KEY
import com.fin_group.aslzar.util.hideKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        api = ApiClient()
        api.init(sessionManager)

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)

        loginEt = binding.editLogin
        passwordEt = binding.editPassword
        progressBar = binding.progressBar2

        binding.root.setOnClickListener{
            it.hideKeyboard()
        }

        requireContext().hideKeyboard(binding.root)

        val secureRandom = SecureRandom()
        val keyBytes = ByteArray(32)
        secureRandom.nextBytes(keyBytes)
        val encryptionKey = SecretKeySpec(keyBytes, "AES")
        val savingKey = Base64.encodeToString(encryptionKey.encoded, Base64.DEFAULT)

        encryptionManager = EncryptionManager(encryptionKey)

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
                            if (loginResponse != null){
                                val encryptedLogin = encryptionManager.encryptData(login)
                                val encryptedPassword = encryptionManager.encryptData(password)
                                sessionManager.saveKey(savingKey)

                                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                editor.putBoolean(IS_LOGGED_IN_KEY, true)
                                editor.apply()

                                sessionManager.saveLogin(encryptedLogin)
                                sessionManager.savePassword(encryptedPassword)

                                sessionManager.saveToken(loginResponse.access_token)
                                sessionManager.saveUserLocation(loginResponse.location)
                                sessionManager.saveName(loginResponse.fio)
                                sessionManager.saveSalesPlan(loginResponse.sales_plan)
                                sessionManager.saveNumberPhone(loginResponse.phone_number)
                                sessionManager.saveEmail(loginResponse.mail)
                                sessionManager.saveLocationId(loginResponse.location_id)
                                sessionManager.saveCheck(loginResponse.check)
                                progressBar.visibility = VISIBLE

                                Toast.makeText(requireContext(), "Добро пожаловать ${loginResponse.fio}!", Toast.LENGTH_SHORT).show()

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
                            } else if (response.code() == 500){
                                binding.progressBar2.visibility = View.GONE
                                Toast.makeText(requireContext(),"Повторите попытку позже, сервер временно не работает",Toast.LENGTH_SHORT).show()
                                api.clearPassLogin()
                                loginEt.setText("")
                                passwordEt.setText("")
                            }else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), "Ошибка при входе", Toast.LENGTH_SHORT).show()
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

        binding.tvForgotPassword.setOnClickListener {
            val fragment = ForgotPasswordFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentLogin, fragment)
            transaction.addToBackStack(null)
            transaction.commit()


        }

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