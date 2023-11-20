package com.fin_group.aslzar.ui.fragments.profile.functions

import android.graphics.Color
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fin_group.aslzar.R
import com.fin_group.aslzar.cipher.EncryptionManager
import com.fin_group.aslzar.response.ResponseForgotPassword
import com.fin_group.aslzar.response.SalesPlanResponse
import com.fin_group.aslzar.ui.dialogs.ChangePasswordProfileFragmentDialog
import com.fin_group.aslzar.ui.fragments.CodeFragment
import com.fin_group.aslzar.ui.fragments.forgotPassword.ForgotPasswordFragment
import com.fin_group.aslzar.ui.fragments.profile.ProfileFragment
import com.github.anastr.speedviewlib.components.Style
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.crypto.spec.SecretKeySpec

fun ProfileFragment.speedometerView(speed: Float) {
    speedometer.speedTo(speed)
    speedometer.makeSections(3, Color.CYAN, Style.BUTT)
    speedometer.sections[0].color = Color.parseColor("#8e5234")
    speedometer.sections[1].color = Color.parseColor("#b66d32")
    speedometer.sections[2].color = Color.parseColor("#f2c1ad")
    speedometer.ticks = arrayListOf(0f, .25f, .5f, .75f, 1f)
}

//fun ProfileFragment.goToChangePasswordDialog() {
//    val changeDataPassword = ChangePasswordProfileFragmentDialog()
//    val fragmentManager: FragmentManager? = activity?.supportFragmentManager
//    fragmentManager?.let {
//        val transaction: FragmentTransaction = it.beginTransaction()
//        transaction.addToBackStack(null)
//        changeDataPassword.show(transaction, "ChangePasswordProfileDialogFragment")
//    }
//}


fun ProfileFragment.goToDialogShow(login: String, password: String) {
    val changeDataPassword = ChangePasswordProfileFragmentDialog.newInstancePass(login, password, "change")
    val fragmentManager: FragmentManager? = activity?.supportFragmentManager
    fragmentManager?.let {
        val transaction: FragmentTransaction = it.beginTransaction()
        transaction.addToBackStack(null)
        changeDataPassword.show(transaction, "ChangePasswordProfileDialogFragment")
    }
}


fun ProfileFragment.changePassword() {
    val call = apiService.getApiService().forgotPassword("Bearer ${sessionManager.fetchToken()}")
    try {
        call.enqueue(object : Callback<ResponseForgotPassword?> {
            override fun onResponse(
                call: Call<ResponseForgotPassword?>,
                response: Response<ResponseForgotPassword?>
            ) {
                if (response.isSuccessful) {
                    val changePassword = response.body()
                    Log.d("TAG", "onResponse: $changePassword")
                    if (changePassword != null) {
                        if (changePassword.result) {
                            key = sessionManager.fetchKey()!!
                            keyBase64 = Base64.decode(key, Base64.DEFAULT)
                            encryptionKey = SecretKeySpec(keyBase64, "AES")
                            encryptionManager = EncryptionManager(encryptionKey)

                            val llogin =
                                encryptionManager.decryptData(sessionManager.fetchLogin()!!)
                            val ppasword =
                                encryptionManager.decryptData(sessionManager.fetchPassword()!!)

                            goToDialogShow(llogin, ppasword)

                        } else {
                            Log.d("TAG", "onResponse: ${response.code()}")
                            Log.d("TAG", "onResponse: ${response.body()}")
                        }
                    } else {
                        Log.d("TAG", "onResponse: ${response.code()}")
                        Log.d("TAG", "onResponse: ${response.body()}")
                    }

                } else {
                    Log.d("TAG", "onResponse: ${response.code()}")
                    Log.d("TAG", "onResponse: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ResponseForgotPassword?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "changePassword: ${e.message}")
    }
}


fun ProfileFragment.getSalesPlan(){
    swipeRefreshLayout.isRefreshing = true
    try {
        val call = apiService.getApiService().getSalesPlan(token = "Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<SalesPlanResponse?> {
            override fun onResponse(
                call: Call<SalesPlanResponse?>,
                response: Response<SalesPlanResponse?>
            ) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful){
                    val salesPlan = response.body()
                    if (salesPlan?.result != null){
                        salesPlanNumber = salesPlan.result.percent
                    } else {
                        Toast.makeText(requireContext(), "Ответ пустой", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<SalesPlanResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }catch (e: Exception){
        Log.d("TAG", "getSalesPlan: ${e.message}")
        swipeRefreshLayout.isRefreshing = false
    }
}