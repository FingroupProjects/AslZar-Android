package com.fin_group.aslzar.ui.dialogs

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fin_group.aslzar.R
import com.fin_group.aslzar.api.ApiClient
import com.fin_group.aslzar.databinding.FragmentDialogChangePasswordProfileBinding
import com.fin_group.aslzar.response.Auth
import com.fin_group.aslzar.response.ResponseChangePassword
import com.fin_group.aslzar.ui.fragments.login.FragmentLogin
import com.fin_group.aslzar.ui.fragments.login.forgotPassword.ForgotPasswordFragment
import com.fin_group.aslzar.util.BaseDialogFragment
import com.fin_group.aslzar.util.setWidthPercent
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ChangePasswordProfileFragmentDialog : BaseDialogFragment() {

    private var _binding: FragmentDialogChangePasswordProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var newPasswordInputLayout: TextInputLayout
    private lateinit var newPasswordEditText: TextInputEditText
    lateinit var apiService: ApiClient
    private var vlLogin: String? = null
    private var vlPassword: String? = null

    companion object {
        fun newInstancePass(login: String, password: String): ChangePasswordProfileFragmentDialog {
            val dialog = ChangePasswordProfileFragmentDialog()
            val args = Bundle()
            args.putString(ARG_LOGIN, login)
            args.putString(ARG_PASSWORD, password)
            dialog.arguments = args
            return dialog
        }

        private const val ARG_LOGIN = "login"
        private const val ARG_PASSWORD = "password"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogChangePasswordProfileBinding.inflate(inflater, container, false)
        apiService = ApiClient()
        arguments?.let {
            vlLogin = it.getString(ARG_LOGIN, "")
            vlPassword = it.getString(ARG_PASSWORD, "")
        }
        Log.d("TAG", "onCreateView: $vlLogin")
        Log.d("TAG", "onCreateView: $vlPassword")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(90)
        newPasswordInputLayout = binding.newPasswordChange
        newPasswordEditText = binding.editNewPasswordChange
        passwordCheck()
        isCancelable = false
//        binding.floatingActionButton.setOnClickListener {
//            dismiss()
//        }
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
                changePasswordWithApi(vlLogin!!, vlPassword!!, newPasswordText)
                dismiss()
                Toast.makeText(requireContext(),"Ваш пароль изменён!", Toast.LENGTH_SHORT).show()
                gotoLoginFragment(requireActivity() as AppCompatActivity)
            } else {
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun gotoLoginFragment(activity: AppCompatActivity) {

        val forgotPasswordFragment = ForgotPasswordFragment()
        val fragmentManager = activity.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentLogin, forgotPasswordFragment)
        transaction.addToBackStack(null)
        transaction.commit()


        Handler().postDelayed({
            val fragmentLogin = FragmentLogin()
            val transactionLogin = fragmentManager.beginTransaction()
            transactionLogin.replace(R.id.fragmentLogin, fragmentLogin)
            transactionLogin.addToBackStack(null)
            transactionLogin.commit()
        }, 2500)

    }

    private fun changePasswordWithApi(login: String, password: String, newPassword: String) {
        val call = apiService.getApiServiceLogin(login, password).changePassword(newPassword)
        try {
            call.enqueue(object : Callback<ResponseChangePassword?> {
                override fun onResponse(
                    call: Call<ResponseChangePassword?>,
                    response: Response<ResponseChangePassword?>
                ) {
                    if (response.isSuccessful) {
                        val response = response.body()
                        Log.d("TAG", "onResponse: $response")
                        if (response != null) {
                            if (response.result) {
                                Log.d("Tag", "Your password is change!")
                                val snackBar = Snackbar.make(binding.root, "Hello", Snackbar.LENGTH_SHORT)
                                snackBar.show()
                            } else {
                                Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseChangePassword?>, t: Throwable) {
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("TAG", "createLead: ${e.message}")
        }
    }

    override fun onStart() {
        super.onStart()
    }
}