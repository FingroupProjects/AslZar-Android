package com.fin_group.aslzar.ui.fragments

import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fin_group.aslzar.R
import com.fin_group.aslzar.cipher.EncryptionManager
import com.fin_group.aslzar.databinding.FragmentCodeBinding
import com.fin_group.aslzar.response.ResponseForgotPassword
import com.fin_group.aslzar.ui.dialogs.ChangePasswordProfileFragmentDialog
import com.fin_group.aslzar.ui.fragments.forgotPassword.ForgotPasswordFragment
import com.fin_group.aslzar.ui.fragments.login.FragmentLogin
import com.fin_group.aslzar.ui.fragments.profile.ProfileFragment
import com.fin_group.aslzar.util.FunCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.crypto.spec.SecretKeySpec


fun CodeFragment.checkCode(randomNumber: String, binding: FragmentCodeBinding, ) {

    val textOne = binding.addOne.text.toString().trim()
    val textTwo = binding.addTwo.text.toString().trim()
    val textThree = binding.addThird.text.toString().trim()
    val textFour = binding.addFour.text.toString().trim()
    val textFive = binding.addFive.text.toString().trim()

    if (textOne == randomNumber[0].toString() &&
        textTwo == randomNumber[1].toString() &&
        textThree == randomNumber[2].toString() &&
        textFour == randomNumber[3].toString() &&
        textFive == randomNumber[4].toString()
    ) {
        passwordLayout.visibility = VISIBLE
        codeLayout.visibility = GONE
    } else {
        passwordLayout.visibility = GONE
        codeLayout.visibility = VISIBLE
        binding.tvErrorNumber.visibility = VISIBLE
        binding.tvErrorNumber.text = "Пин код введен не правильно!"
    }
}

fun CodeFragment.dialogShow(binding: FragmentCodeBinding) {

    val key = sessionManager.fetchKey()!!
    val keyBase64 = Base64.decode(key, Base64.DEFAULT)
    val encryptionKey = SecretKeySpec(keyBase64, "AES")
    val encryptionManager = EncryptionManager(encryptionKey)

    val llogin = encryptionManager.decryptData(sessionManager.fetchLogin()!!)

    val ppasword = encryptionManager.decryptData(sessionManager.fetchPassword()!!)

    binding.tvErrorNumber.visibility = View.GONE

    val changeDataPassword = ChangePasswordProfileFragmentDialog.newInstancePass(llogin, ppasword, "change")
    val fragmentManager: FragmentManager? = activity?.supportFragmentManager
    fragmentManager?.let {
        val transaction: FragmentTransaction = it.beginTransaction()
        changeDataPassword.show(transaction, "ChangePasswordProfileDialogFragment")
    }
}


fun CodeFragment.startCountDawnTimer(binding: FragmentCodeBinding, timeMillis: Long){

    var timer: CountDownTimer? = null

    timer = object : CountDownTimer(timeMillis, 1000){
        override fun onTick(timeM: Long) {
            val minutes = timeM / 1000 / 60
            val seconds = (timeM / 1000) % 60
            val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
            binding.timer.text = timeLeftFormatted
        }

        override fun onFinish() {
            binding.timer.text = "Отправить ещё раз"
            binding.timer.setOnClickListener {

                forgotPassword(binding, object : FunCallback {
                    override fun onSuccess(success: Boolean) {
                        if (success){
                        }
                    }
                    override fun onError(errorMessage: String) {
                        if (errorMessage == "Ошибка"){
                            Toast.makeText(requireContext(), "Такого эл. почты нет!", Toast.LENGTH_SHORT).show()
                        }else if(errorMessage == "Ошибка с сервером"){
                            Toast.makeText(requireContext(), "Повторите попытку позже", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                checkNumber(binding)
                start()
                binding.timer.setOnClickListener(null)
            }
        }
    }.start()
}

fun CodeFragment.checkNumber(binding: FragmentCodeBinding){

    binding.tvErrorNumber.visibility = View.GONE
    binding.btnPass.visibility = View.VISIBLE
    binding.timer.visibility = View.VISIBLE
    startCountDawnTimer(binding,60000)

}

fun CodeFragment.forgotPassword(binding: FragmentCodeBinding, callback: FunCallback) {

    val call  = apiService.getApiService().forgotPassword("Bearer ${sessionManager.fetchToken()}")

    try {
        call.enqueue(object : Callback<ResponseForgotPassword?> {
            override fun onResponse(
                call: Call<ResponseForgotPassword?>,
                response: Response<ResponseForgotPassword?>
            ) {
                if (response.code() == 200){
                    if (response.isSuccessful){
                        val password = response.body()
                        Log.d("TAG", "onResponse: $password")
                        if (password != null){
                            if (password.result){
                                callback.onSuccess(true)
                                textWatcher(binding)

                                binding.btnPass.setOnClickListener {
                                    checkCode(password.code.toString(), binding)
                                }

                            }else{
                                callback.onSuccess(false)

                                Log.d("TAG", "onResponse: ${response.code()}")
                                Log.d("TAG", "onResponse: ${response.body()}")
                            }
                        }else{
                            callback.onSuccess(false)
                        }
                    }
                }else if (response.code() == 500){
                    Toast.makeText(requireContext(), "Повторите попитку позже", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseForgotPassword?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })

    }catch (e: Exception){
        Log.d("TAG", "createLead: ${e.message}")
    }
}
private fun textWatcher(binding: FragmentCodeBinding){

    val editTexts = arrayOf(binding.addOne, binding.addTwo, binding.addThird, binding.addFour, binding.addFive)

    for (i in editTexts.indices) {
        val currentEditText = editTexts[i]

        currentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                } else if (s!!.isEmpty() && i > 0) {
                    editTexts[i - 1].requestFocus()
                }
            }
        })
    }
}
