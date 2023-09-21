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
import com.fin_group.aslzar.R
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorV2Binding
import com.fin_group.aslzar.models.TypePay
import com.fin_group.aslzar.response.Client
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragmentV2
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.formatNumber


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

            textWatchers(binding, percentInstallment, vlTotalPrice)
//            createTable(binding, vlTotalPrice)
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
            bonusClient.text = "${formatNumber(selectedClient.bonus)} UZS"
            paymentClient(selectedClient, binding, percentInstallment)
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

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.createTable(binding: FragmentCalculatorV2Binding, totalPrice: Number){
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
                text = formatNumber(monthPayment)
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
    val maxValueBonus: Double = (totalPrice.toDouble() * percent.payment_bonus.toDouble()) / 100
    val minValueFirstPay: Double = (totalPrice.toDouble() * percent.first_pay.toDouble()) / 100
    binding.payWithFirstPay.text = "${formatNumber(minValueFirstPay)} UZS"
    binding.firstPay.setText(minValueFirstPay.toString())

    val textWatcherForBonus = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newText = s.toString().trim()
            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDouble()
                if (currentValue > maxValueBonus) {
                    binding.bonus.setText(maxValueBonus.toString())
                    binding.bonus.setSelection(binding.bonus.length())
                }
            }
            val countText = binding.bonus.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            binding.payWithBonus.text = "${formatNumber(countText)} UZS"
        }

        override fun afterTextChanged(s: Editable?) {
            val newText = s.toString().trim()
            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDouble()
                if (currentValue > maxValueBonus) {
                    binding.bonus.setText(maxValueBonus.toString())
                    binding.bonus.error = "Вводимое число не может превышать ${percent.payment_bonus}% ($maxValueBonus) от итоговой суммы"
                }
            }
            val countText = binding.bonus.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            val countText2 = binding.firstPay.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            val hello = countText + countText2

            binding.totalPrice.text = "${formatNumber(totalPrice.toDouble() - hello)} UZS"
            binding.payWithBonus.text = "${formatNumber(countText)} UZS"
        }
    }
    binding.bonus.addTextChangedListener(textWatcherForBonus)

    val textWatcherForFirstPay = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newText = s.toString().trim()
            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDouble()
                if (currentValue < minValueFirstPay) {
//                    binding.firstPay.setText(minValueFirstPay.toString())
                    binding.firstPay.error = "Минимальное значение первоначального взноса ${percent.first_pay}% ($minValueFirstPay) от итоговой суммы"
                }
                if (currentValue > totalPrice.toDouble()) {
                    binding.firstPay.setText(totalPrice.toString())
                    binding.firstPay.error = "Первоначальный взнос не может превышать сумму покупки"
                }
            }
            val countText =
                binding.firstPay.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            binding.payWithFirstPay.text = "${formatNumber(countText)} UZS"
        }

        override fun afterTextChanged(s: Editable?) {
            val newText = s.toString().trim()
            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDouble()
                if (currentValue < minValueFirstPay) {
//                    binding.firstPay.setText(minValueFirstPay.toString())
                    binding.firstPay.error =
                        "Минимальное значение первоначального взноса ${percent.first_pay}% ($minValueFirstPay) от итоговой суммы"
                }
                if (currentValue > totalPrice.toDouble()) {
                    binding.firstPay.setText(totalPrice.toString())
                    binding.firstPay.error = "Первоначальный взнос не может превышать сумму покупки"
                }
            }
            val countText = binding.firstPay.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            val countText2 = binding.bonus.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            val hello = countText + countText2
            vlTotalPrice.toDouble() - hello

            binding.totalPrice.text = "${formatNumber(totalPrice.toDouble() - hello)} UZS"
            binding.payWithFirstPay.text = "${formatNumber(countText)} UZS"
        }
    }

    binding.firstPay.addTextChangedListener(textWatcherForFirstPay)
    createTable(binding, totalPrice)
}