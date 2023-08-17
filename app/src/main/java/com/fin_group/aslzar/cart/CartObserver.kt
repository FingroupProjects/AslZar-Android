package com.fin_group.aslzar.cart

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.lifecycle.Observer
import java.text.DecimalFormat
import java.text.NumberFormat

class CartObserver(private val textView: TextView) : Observer<Double> {
    private val currencyFormat = NumberFormat.getCurrencyInstance()
    @SuppressLint("SetTextI18n")
    override fun onChanged(value: Double) {
        value.let {
            val formattedValue = currencyFormat.format(value)
            textView.text = "$formattedValue UZS"
//            val decimalFormat = DecimalFormat("#.0")
//            val formattedNumber = decimalFormat.format(it)
//            textView.text = "$formattedNumber UZS"
        }
    }
}