package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
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
        }
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragmentV2.fetchClientsAndTypePay(binding: FragmentCalculatorV2Binding) {
    percentInstallment = PercentInstallment(
        90, 15, listOf(
            Percent(6.9, 3),
            Percent(8.9, 6),
            Percent(12.9, 9),
            Percent(17.9, 12),
        )
    )

    clientList = listOf(
        Client("1", "Розничный клиент", 0, 0, "Silver", "Lead", 8400),
        Client("3", "Tohirjon", 37512, 3, "Silver", "Referral", 8400),
        Client("4", "Nuriddin", 19852, 3, "Silver", "Referral", 10000),
        Client("5", "Tursunboy", 0, 0, "", "Lead", 8000),
        Client("8", "Rustam", 7522, 3, "Silver", "Referral", 9500),
        Client("6", "Jamshed", 85654, 3, "Silver", "Referral", 8500),
        Client("7", "Khusrav", 9654, 5, "Gold", "Lead", 4500),
        Client("2", "Suhrob", 150000, 7, "Diamond", "Referral", 5000000),
    )

    val arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, clientList.map { it.client_name })

    binding.apply {

        clientType.setAdapter(arrayAdapterTypeClient)
        clientType.setOnItemClickListener { parent, view, position, id ->
            selectedClient = clientList[position]
            bonusClient.text = "${formatNumber(selectedClient.bonus)} UZS"
            paymentClient(selectedClient, binding, percentInstallment)

            val contains = "lead"
            val containsSubstring = selectedClient.client_type.contains(contains, true)

            if (containsSubstring){
                cbBonus.visibility = GONE
                cbBonus.isChecked = false
                bonus.setText("")
            } else {
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
    }
}


fun CalculatorFragmentV2.paymentClient(
    client: Client,
    binding: FragmentCalculatorV2Binding,
    percent: PercentInstallment
) {
    val contains = "lead"
    val containsSubstring = client.client_type.contains(contains, true)

    if (containsSubstring){
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
){

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
fun CalculatorFragmentV2.textWatchers(binding: FragmentCalculatorV2Binding, percent: PercentInstallment, totalPrice: Number){
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
            val countText = binding.firstPay.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            binding.payWithFirstPay.text = "${formatNumber(countText)} UZS"
        }

        override fun afterTextChanged(s: Editable?) {
            val newText = s.toString().trim()
            if (!newText.isNullOrEmpty()) {
                val currentValue = newText.replace(',', '.').toDouble()
                if (currentValue < minValueFirstPay) {
//                    binding.firstPay.setText(minValueFirstPay.toString())
                    binding.firstPay.error = "Минимальное значение первоначального взноса ${percent.first_pay}% ($minValueFirstPay) от итоговой суммы"
                }
            }
            val countText = binding.firstPay.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0
            val countText2 = binding.bonus.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0

            val hello = countText + countText2

            binding.totalPrice.text = "${formatNumber(totalPrice.toDouble() - hello)} UZS"

            binding.payWithFirstPay.text = "${formatNumber(countText)} UZS"
        }
    }

    binding.firstPay.addTextChangedListener(textWatcherForFirstPay)
}