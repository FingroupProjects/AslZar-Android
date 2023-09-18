
package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.util.Log
import android.widget.ArrayAdapter
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentCalculatorV2Binding
import com.fin_group.aslzar.models.TypePay
import com.fin_group.aslzar.response.Client
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
        }
    }
}

fun CalculatorFragmentV2.fetchClientsAndTypePay(binding: FragmentCalculatorV2Binding){
    allTypePay = listOf(
        TypePay(1, "Наличными"),
        TypePay(2, "В рассрочку")
    )

    clientList = listOf(
        Client("1", "Реферальный покупатель", 1500, "Silver", "Lead", 1000),
        Client("3", "Tohirjon", 1500, "Silver", "Lead", 8400),
        Client("4", "Nuriddin", 1500, "Silver", "Referral", 10000),
        Client("5", "Tursunboy", 1500, "Silver", "Lead", 8000),
        Client("8", "Rustam", 1500, "Silver", "Referral", 9500),
        Client("6", "Jamshed", 1500, "Silver", "Referral", 8500),
        Client("7", "Khusrav", 1500, "Silver", "Lead", 4500),
        Client("2", "Suhrob", 150000, "Silver", "Referral", 5000000),
    )

    val arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, clientList.map { it.client_name })
    val arrayAdapterTypePay =
        ArrayAdapter(requireContext(), R.layout.spinner_item, allTypePay.map { it.name })

    binding.apply {
        typePay.setAdapter(arrayAdapterTypePay)
        typePay.setOnItemClickListener { parent, view, position, id ->
            typePaySelect = allTypePay[position].id
        }

        clientType.setAdapter(arrayAdapterTypeClient)
        clientType.setOnItemClickListener { parent, view, position, id ->
//            val selectedClientName = clientList[position].client_name
//            selectedClient = clientList.find { it.client_name == selectedClientName } ?: clientList.first()
            selectedClient = clientList[position]
        }
    }
}


//fun CalculatorFragmentV2.paymentClient(client: Client, typePay: TypePay, binding: FragmentCalculatorV2Binding){
//    if (typePay.id == 1){
//        if (client.)
//        binding.apply {
//
//        }
//    } else if (typePay.id == 2){
//
//    }
//
//
//}