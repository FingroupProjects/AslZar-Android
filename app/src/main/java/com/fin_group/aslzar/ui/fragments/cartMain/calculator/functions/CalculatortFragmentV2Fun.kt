package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View.GONE
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import com.fin_group.aslzar.R
import com.fin_group.aslzar.adapter.TableInstallmentAdapter
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorV2Binding
import com.fin_group.aslzar.models.TypePay
import com.fin_group.aslzar.response.Client
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragmentV2
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.formatNumber
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log


fun CalculatorFragmentV2.cartObserver(binding: FragmentCalculatorV2Binding) {
    cartObserver = object : CartObserver {
        @SuppressLint("SetTextI18n")
        override fun onCartChanged(
            totalPriceWithoutSale: Number,
            totalPriceWithSale: Number,
            totalCount: Int,
            totalPrice: Number
        ) {
            binding.totalPriceWithoutSale.text = "${formatNumber(totalPriceWithoutSale)} UZS"
            binding.sumSale.text = "${formatNumber(totalPriceWithSale)} UZS"
            binding.totalPrice.text = "${formatNumber(totalPrice)} UZS"

            vlTotalPrice = totalPrice
            vlTotalPriceWithSale = totalPriceWithSale
            vlTotalPriceWithoutSale = totalPriceWithoutSale
            vlTotalPriceSale = totalPriceWithSale

            val hello = totalPrice.toDouble() - sessionManager.fetchCheck().toDouble()
            Log.d("TAG", "onCartChanged: $hello")
            Log.d("TAG", "onCartChanged: ${sessionManager.fetchCheck().toDouble()}")
            Log.d("TAG", "onCartChanged: ${totalPrice.toDouble()}")
            averageBill.text = "Для среднего чека осталось $hello"

            textWatchers(binding, percentInstallment, totalPrice)
            printPercent(binding, percentInstallment, totalPrice)
        }
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.fetchClientsAndTypePay(binding: FragmentCalculatorV2Binding) {

    val arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, clientList.map { it.client_name })

    binding.apply {
        clientType.setAdapter(arrayAdapterTypeClient)
        clientType.setOnItemClickListener { parent, view, position, id ->
            selectedClient = clientList[position]
            bonusClient.text = "${formatNumber(selectedClient!!.bonus)} UZS"
            paymentClient(selectedClient!!, binding, percentInstallment)
            textWatchers(binding, percentInstallment, vlTotalPrice)
        }
    }
}


fun CalculatorFragmentV2.paymentClient(
    client: Client,
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment
) {
    val contains = "lead"
    val containsSubstring = client.client_type.contains(contains, true)

    if (containsSubstring) {
        binding.cbBonus.visibility = GONE
        binding.cbBonus.isChecked = false
        binding.bonus.setText("")
    } else {
        installmentPayReferralClient(client, binding, percent, vlTotalPrice)
        textWatchers(binding, percent, Cart.getTotalPrice())
    }
    textWatchers(binding, percentInstallment, vlTotalPrice)
}

fun CalculatorFragmentV2.installmentPayLeadClient(binding: FragmentCalculatorV2Binding) {

}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.installmentPayReferralClient(
    client: Client,
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment,
    totalPrice: Number
) {

    binding.apply {
        cbBonus.visibility = VISIBLE
        cbBonus.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tilBonus.visibility = VISIBLE
            } else {
                tilBonus.visibility = GONE
                bonus.setText("")
            }
        }
    }
}

fun CalculatorFragmentV2.printPercent(binding: FragmentCalculatorV2Binding, installment: PercentInstallment, totalPrice: Number){
    binding.apply {
        rvPayments.layoutManager = LinearLayoutManager(requireContext())
        adapterPaymentPercent = TableInstallmentAdapter(installment, totalPrice)
        rvPayments.adapter = adapterPaymentPercent

    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.createTable(binding: FragmentCalculatorV2Binding, totalPrice: Number){
    monthLinearLayout.removeAllViews()
    percentLinearLayout.removeAllViews()

    binding.apply {
        val monthLinearLayout = monthTable
        val percentLinearLayout = percentTable

        for (percent in percentInstallment.result){
            val indexPercent = percentInstallment.result.indexOf(percent)

            val textViewMonth = TextView(requireContext())
            textViewMonth.apply {
                text = "${percent.mounth} платежей (${percent.coefficient}%)"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                setPadding(15,15,15,15)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_1))
                TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                if (indexPercent < percentInstallment.result.size - 1) {
                    setBackgroundResource(R.drawable.bg_text_view_in_table)
                }
            }
            monthLinearLayout.addView(textViewMonth)

            val textViewPercent = TextView(requireContext())
            val monthPayment = (((totalPrice.toDouble() * percent.coefficient.toDouble()) / 100) + totalPrice.toDouble()) / percent.mounth.toDouble()
            textViewPercent.apply {
                text = "${formatNumber(monthPayment)} UZS"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                setPadding(15,15,15,15)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_1))
                TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                if (indexPercent < percentInstallment.result.size - 1) {
                    setBackgroundResource(R.drawable.bg_text_view_in_table)
                }
            }
            percentLinearLayout.addView(textViewPercent)
        }
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.textWatchers(
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment,
    totalPrice: Number
) {
    // Кэширование элементов интерфейса
    val bonusEditText = binding.bonus
    val firstPayEditText = binding.firstPay
    val payWithFirstPayTextView = binding.payWithFirstPay
    val payWithBonusTextView = binding.payWithBonus
    val totalPriceTextView = binding.totalPrice

    val maxValueBonus: Double = (totalPrice.toDouble() * percent.payment_bonus.toDouble()) / 100
    val minValueFirstPay: Double = (totalPrice.toDouble() * percent.first_pay.toDouble()) / 100
    firstPayEditText.setText(formatNumber(minValueFirstPay))

    var finalTotalPrice = totalPrice.toDouble()
    var countTextBonus = 0.0
    var countTextFirstPay = 0.0

    fun updateDisplayedValues() {
        val bonusAmount = (finalTotalPrice * percent.payment_bonus.toDouble()) / 100
        val firstPayAmount = (finalTotalPrice * percent.first_pay.toDouble()) / 100

        // Если введены значения бонуса и первоначального взноса, учитываем их
        val enteredBonus = bonusEditText.text.toString().toDoubleOrNull() ?: 0.0
        val enteredFirstPay = firstPayEditText.text.toString().toDoubleOrNull() ?: 0.0

        val remainingTotal = finalTotalPrice - enteredFirstPay
        val bonusPercentOfRemaining = (remainingTotal * percent.payment_bonus.toDouble()) / 100

        if (enteredBonus > bonusPercentOfRemaining) {
            bonusEditText.setText(bonusPercentOfRemaining.toString())
            bonusEditText.setSelection(bonusEditText.length())
            countTextBonus = bonusPercentOfRemaining
        } else {
            countTextBonus = enteredBonus
        }

        val newTotalPrice = remainingTotal - countTextBonus

        totalPriceTextView.text = "${formatNumber(newTotalPrice)} UZS"
        payWithBonusTextView.text = "${formatNumber(countTextBonus)} UZS"
        payWithFirstPayTextView.text = "${formatNumber(enteredFirstPay)} UZS"

        adapterPaymentPercent.updateData(percent, newTotalPrice)
    }

    textWatcherForBonus = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val newText = s.toString().trim()
            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDouble()
                if (currentValue > maxValueBonus) {
                    bonusEditText.setText(maxValueBonus.toString())
                    bonusEditText.setSelection(bonusEditText.length())
                }
            }
            updateDisplayedValues()
        }
    }

    textWatcherForFirstPay = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val newText = s.toString().trim()
            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDouble()
                if (currentValue < minValueFirstPay) {
                    firstPayEditText.error = "Минимальное значение первоначального взноса ${percent.first_pay}% ($minValueFirstPay) от итоговой суммы"
                } else if (currentValue > totalPrice.toDouble()) {
                    firstPayEditText.setText(totalPrice.toString())
                    firstPayEditText.error = "Первоначальный взнос не может превышать сумму покупки"
                } else {
                    firstPayEditText.error = null
                }
            }
            updateDisplayedValues()

        }
    }

    bonusEditText.addTextChangedListener(textWatcherForBonus)
    firstPayEditText.addTextChangedListener(textWatcherForFirstPay)

    updateDisplayedValues()
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.resetCalculator(binding: FragmentCalculatorV2Binding) {
    binding.apply {
        try {
            cbBonus.isChecked = false
            cbBonus.visibility = GONE

            bonus.setText("")
            firstPay.setText("")

            val initialTotalPrice = Cart.getTotalPrice()
            totalPrice.text = "${formatNumber(initialTotalPrice)} UZS"
            payWithBonus.text = "0.00 UZS"
            payWithFirstPay.text = "0.00 UZS"

            printPercent(binding, percentInstallment, initialTotalPrice)
        } catch (e: Exception) {
            Log.d("TAG", "resetCalculator: ${e.message}")
        } catch (e: NumberFormatException) {
            Log.d("TAG", "resetCalculator: ${e.message}")
        }
    }
}



//fun CalculatorFragmentV2.fetchCoefficientPlanFromApi() {
//    try {
//        val call = api.getApiService().getPercentAndMonth("Bearer ${sessionManager.fetchToken()}")
//        call.enqueue(object : Callback<PercentInstallment?> {
//            override fun onResponse(
//                call: Call<PercentInstallment?>,
//                response: Response<PercentInstallment?>
//            ) {
//                if (response.isSuccessful) {
//                    val coefficientPlanList = response.body()
//                    if (coefficientPlanList != null) {
//                        val coefficientPlanJson = Gson().toJson(coefficientPlanList.result)
//                        prefs.edit().putString("coefficientPlan", coefficientPlanJson).apply()
//                        Log.d("TAG", "onResponse: $coefficientPlanList")
////                        percentInstallment = coefficientPlanList
//                    }
//                }
//            }
//            override fun onFailure(call: Call<PercentInstallment?>, t: Throwable) {
//                Log.d("TAG", "onFailure fetchCategoriesFromApi: ${t.message}")
//            }
//        })
//    } catch (e: Exception) {
//        Log.d("TAG", "fetchCategoriesFromApi: ${e.message}")
//    }
//}