package com.fin_group.aslzar.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentCodeBinding
import com.fin_group.aslzar.response.ResponseChangePassword
import com.fin_group.aslzar.util.FunCallback
import com.fin_group.aslzar.util.SessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class CodeFragment : Fragment() {

    private var _binding: FragmentCodeBinding? = null
    private val binding get() = _binding!!

    lateinit var apiService: ApiClient
    lateinit var sessionManager: SessionManager

    lateinit var newPasswordInputLayout: TextInputLayout
    lateinit var newPasswordEditText: TextInputEditText

    lateinit var codeLayout: ConstraintLayout
    lateinit var passwordLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCodeBinding.inflate(inflater, container, false)

        apiService = ApiClient()
        sessionManager = SessionManager(requireContext())
        apiService.init(sessionManager)
        codeLayout = binding.codeValidationLayout
        passwordLayout = binding.newPasswordLayout

        checkNumber(binding)

        forgotPassword(binding, object : FunCallback {
            override fun onSuccess(success: Boolean) {
                if (success) {
                    Toast.makeText(requireContext(), "Все хорошо", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onError(errorMessage: String) {
                if (errorMessage == "Ошибка") {
                    Toast.makeText(requireContext(), "Такого эл. почты нет!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newPasswordInputLayout = binding.newPasswordChange
        newPasswordEditText = binding.editNewPasswordChange
        passwordCheck()
    }

    private fun passwordCheck() {
        val newPassword = binding.newPasswordChange
        val repeatPassword = binding.repeatNewPasswordChange
        val saveButton = binding.btnSaveChangeData

        saveButton.setOnClickListener {
            val newPasswordText = newPassword.editText?.text.toString()
            val repeatPasswordText = repeatPassword.editText?.text.toString()

            if (newPasswordText.isEmpty()) {
                binding.editNewPasswordChange.error = "Введите новый пароль!"
                binding.editNewPasswordChange.requestFocus()
                return@setOnClickListener
            } else if (newPasswordText.length < 4) {
                binding.editNewPasswordChange.error = "Минимальная длина паролья 4 символов"
                binding.editNewPasswordChange.requestFocus()
                return@setOnClickListener
            } else if (repeatPasswordText.isEmpty()) {
                binding.editRepeatNewPasswordChange.error = "Повторите новый пароль!"
                binding.editRepeatNewPasswordChange.requestFocus()
                return@setOnClickListener
            }
            if (newPasswordText == repeatPasswordText) {
                changePassword(newPasswordText, object : FunCallback {
                    override fun onSuccess(success: Boolean) {
                        if (success) {
                            Handler().postDelayed({
                                findNavController().popBackStack()
                            }, 2000)
                            Toast.makeText(
                                requireContext(),
                                "Ваш пароль изменён.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Не удалось изменить пароль, повторите попытку.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(errorMessage: String) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                })


            } else {
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun changePassword(newPassword: String, callback: FunCallback) {
        val call = apiService.getApiService()
            .changePassword2(newPassword, "Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<ResponseChangePassword?> {
            override fun onResponse(
                call: Call<ResponseChangePassword?>,
                response: Response<ResponseChangePassword?>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.result) {
                        callback.onSuccess(true)
                    } else {
                        callback.onSuccess(false)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseChangePassword?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
                callback.onError(t.message.toString())
            }
        })
    }

}