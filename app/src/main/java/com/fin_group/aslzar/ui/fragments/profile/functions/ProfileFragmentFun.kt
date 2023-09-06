package com.fin_group.aslzar.ui.fragments.profile.functions

import android.graphics.Color
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fin_group.aslzar.response.SalesPlanResponse
import com.fin_group.aslzar.ui.dialogs.ChangePasswordProfileFragmentDialog
import com.fin_group.aslzar.ui.fragments.profile.ProfileFragment
import com.github.anastr.speedviewlib.components.Style
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun ProfileFragment.speedometerView(speed: Float) {


    speedometer.speedTo(speed)
    speedometer.makeSections(3, Color.CYAN, Style.BUTT)
    speedometer.sections[0].color = Color.parseColor("#8e5234")
    speedometer.sections[1].color = Color.parseColor("#b66d32")
    speedometer.sections[2].color = Color.parseColor("#f2c1ad")
    speedometer.ticks = arrayListOf(0f, .25f, .5f, .75f, 1f)
}

fun ProfileFragment.goToChangePasswordDialog() {
    val changeDataPassword = ChangePasswordProfileFragmentDialog()
    val fragmentManager: FragmentManager? = activity?.supportFragmentManager
    fragmentManager?.let {
        val transaction: FragmentTransaction = it.beginTransaction()
        transaction.addToBackStack(null)
        changeDataPassword.show(transaction, "ChangePasswordProfileDialogFragment")
    }
}

fun ProfileFragment.getSalesPlan(){
    progressBar.visibility = VISIBLE

    try {
        val call = apiService.getApiService().getSalesPlan(token = "Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<SalesPlanResponse?> {
            override fun onResponse(
                call: Call<SalesPlanResponse?>,
                response: Response<SalesPlanResponse?>
            ) {
                progressBar.visibility = GONE
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
                progressBar.visibility = GONE
            }
        })
    }catch (e: Exception){
        Log.d("TAG", "getSalesPlan: ${e.message}")
        progressBar.visibility = GONE
    }
}