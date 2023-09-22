package com.fin_group.aslzar.ui.fragments.login.forgotPassword.function

import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fin_group.aslzar.databinding.FragmentForgotPasswordBinding
import com.fin_group.aslzar.ui.dialogs.ChangePasswordProfileFragmentDialog
import com.fin_group.aslzar.ui.fragments.login.forgotPassword.ForgotPasswordFragment

fun ForgotPasswordFragment.checkEmail(binding: FragmentForgotPasswordBinding){


    binding.btnForgotPassword.setOnClickListener {

        val email = binding.editEmail.text.toString()
        binding.timer.visibility = View.GONE

        if (isValidEmail(email)) {

            binding.tvError.visibility = View.GONE
            binding.addOne.visibility = View.VISIBLE
            binding.addTwo.visibility = View.VISIBLE
            binding.addThird.visibility = View.VISIBLE
            binding.addFour.visibility = View.VISIBLE
            binding.addFive.visibility = View.VISIBLE
            binding.tvText.text = "На вашу почту $email отправлен код"
            binding.tvErrorNumber.visibility = View.GONE
            binding.editEmail.visibility = View.GONE

            checkNumber(binding)

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
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@gmail.com")
}

fun ForgotPasswordFragment.checkNumber(binding: FragmentForgotPasswordBinding){

    binding.tvErrorNumber.visibility = View.GONE
    binding.btnForgotPassword.visibility = View.GONE
    binding.btnPass.visibility = View.VISIBLE

    binding.timer.visibility = View.VISIBLE

    startCountDawnTimer(binding,60000)

    val random = java.util.Random()
    val randomNumber = random.nextInt((90000) + 10000).toString()
    Log.d("Tag","Случайное число: $randomNumber")

    binding.btnPass.setOnClickListener {
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

            dialogShow(binding)

        } else {
            binding.tvErrorNumber.visibility = View.VISIBLE
            binding.tvErrorNumber.text = "Пин код введен не правильно!"
        }
    }
}

fun ForgotPasswordFragment.dialogShow(binding: FragmentForgotPasswordBinding) {

    binding.tvErrorNumber.visibility = View.GONE

    val changeDataPassword = ChangePasswordProfileFragmentDialog()
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
                checkNumber(binding)
                start()
                binding.timer.setOnClickListener(null)
            }
        }

    }.start()

}

fun ForgotPasswordFragment.getRandomNumber(){
    Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
}