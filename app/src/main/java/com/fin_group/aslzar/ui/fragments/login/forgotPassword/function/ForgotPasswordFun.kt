package com.fin_group.aslzar.ui.fragments.login.forgotPassword.function

import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fin_group.aslzar.databinding.FragmentForgotPasswordBinding
import com.fin_group.aslzar.response.ForgotPasswordResponse
import com.fin_group.aslzar.ui.dialogs.ChangePasswordProfileFragmentDialog
import com.fin_group.aslzar.ui.fragments.login.forgotPassword.ForgotPasswordFragment
import com.fin_group.aslzar.util.FunCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun ForgotPasswordFragment.checkEmail(binding: FragmentForgotPasswordBinding){

    binding.btnForgotPassword.setOnClickListener {

        binding.progressBar2.visibility = View.VISIBLE

        val email = binding.editEmail.text.toString()
        binding.timer.visibility = View.GONE

        if (isValidEmail(email)) {
            forgotPasswordWithEmail(binding, object : FunCallback {
                override fun onSuccess(success: Boolean) {
                    binding.tvError.visibility = View.GONE
                    binding.addOne.visibility = View.VISIBLE
                    binding.addTwo.visibility = View.VISIBLE
                    binding.addThird.visibility = View.VISIBLE
                    binding.addFour.visibility = View.VISIBLE
                    binding.addFive.visibility = View.VISIBLE
                    binding.tvText.text = "Проверте почту: $email"
                    binding.tvErrorNumber.visibility = View.GONE
                    binding.editEmail.visibility = View.GONE
                    binding.email.visibility = View.GONE
                    binding.progressBar2.visibility = View.GONE
                    checkNumber(binding)
                }

                override fun onError(errorMessage: String) {
                    if (errorMessage == "Ошибка"){
                        Toast.makeText(requireContext(), "Такого эл. почты нет!", Toast.LENGTH_SHORT).show()
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = "Нет пользоваателя с такой электронной почтой"

                    }
                }
            })

        } else {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = "Электронаая почта введен неправильно!"
        }
    }

    binding.editEmail.addTextChangedListener(object : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            binding.tvError.visibility = View.GONE
        }
    })
}

fun isValidEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
fun ForgotPasswordFragment.checkNumber(binding: FragmentForgotPasswordBinding){

    binding.tvErrorNumber.visibility = View.GONE
    binding.btnForgotPassword.visibility = View.GONE
    binding.btnPass.visibility = View.VISIBLE
    binding.timer.visibility = View.VISIBLE
    startCountDawnTimer(binding,60000)

}

fun ForgotPasswordFragment.dialogShow(
    binding: FragmentForgotPasswordBinding,
    login: String,
    password: String
) {

    binding.tvErrorNumber.visibility = View.GONE
    val changeDataPassword = ChangePasswordProfileFragmentDialog.newInstancePass(login, password)
    val fragmentManager: FragmentManager? = activity?.supportFragmentManager
    fragmentManager?.let {
        val transaction: FragmentTransaction = it.beginTransaction()
        changeDataPassword.show(transaction, "ChangePasswordProfileDialogFragment")
    }
}

fun ForgotPasswordFragment.startCountDawnTimer(binding: FragmentForgotPasswordBinding, timeMillis: Long){

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

                forgotPasswordWithEmail(binding, object : FunCallback {
                    override fun onSuccess(success: Boolean) {
                        if (success){
                            Toast.makeText(requireContext(), "Все хорошо", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(errorMessage: String) {
                        if (errorMessage == "Ошибка"){
                            Toast.makeText(requireContext(), "Такого эл. почты нет!", Toast.LENGTH_SHORT).show()
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

fun ForgotPasswordFragment.forgotPasswordWithEmail(binding: FragmentForgotPasswordBinding, callback: FunCallback) {

    val email = binding.editEmail.text.toString()
    val call = apiService.getApiServiceForgotPassword().forgotPasswordWithMail(email)

    try {
        call.enqueue(object : Callback<ForgotPasswordResponse?> {
            override fun onResponse(
                call: Call<ForgotPasswordResponse?>,
                response: Response<ForgotPasswordResponse?>
            ) {

                if (response.code() == 400) {

                    callback.onError("Ошибка")
                    Toast.makeText(requireContext(), "Такого эл. почты нет!", Toast.LENGTH_SHORT).show()

                } else if (response.code() == 200){
                    if (response.isSuccessful) {
                        val getEmail = response.body()
                        Log.d("TAG", "onResponse: $getEmail")
                        if (getEmail != null) {

                            if (getEmail.result) {
                                callback.onSuccess(true)
                                textWatcher(binding)
                                binding.btnPass.setOnClickListener {
                                    checkCode(getEmail.code.toString(), binding, getEmail.login, getEmail.password)
                                }
                            } else {
                                callback.onSuccess(false)

                                Log.d("TAG", "onResponse: ${response.code()}")
                                Log.d("TAG", "onResponse: ${response.body()}")
                            }
                        } else {
                            callback.onSuccess(false)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "createLead: ${e.message}")
    }
}

private fun ForgotPasswordFragment.checkCode(
    randomNumber: String,
    binding: FragmentForgotPasswordBinding,
    login: String,
    password: String
){

    val textOne = binding.addOne.text.toString().trim()
    val textTwo = binding.addTwo.text.toString().trim()
    val textThree = binding.addThird.text.toString().trim()
    val textFour = binding.addFour.text.toString().trim()
    val textFive = binding.addFive.text.toString().trim()

    if (textOne == randomNumber[0].toString() &&
        textTwo == randomNumber[1].toString() &&
        textThree == randomNumber[2].toString() &&
        textFour == randomNumber[3].toString() &&
        textFive == randomNumber[4].toString()) {
        dialogShow(binding, login, password)
    } else {
        binding.tvErrorNumber.visibility = View.VISIBLE
        binding.tvErrorNumber.text = "Пин код введен не правильно!"
    }
}

private fun textWatcher(binding: FragmentForgotPasswordBinding){

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