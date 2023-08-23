package com.fin_group.aslzar.cart

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fin_group.aslzar.util.CartObserver
import java.text.DecimalFormat
import java.text.NumberFormat

//class CartObserver(private val textView: TextView) : Observer<Number> {
//    private val currencyFormat = NumberFormat.getCurrencyInstance()
//    @SuppressLint("SetTextI18n")
//    override fun onChanged(value: Number) {
//        value.let {
////            val formattedValue = currencyFormat.format(value)
//            val decimalFormat = DecimalFormat("#.0")
//            val formattedNumber = decimalFormat.format(it)
//            textView.text = "$formattedNumber UZS"
////            val decimalFormat = DecimalFormat("#.0")
////            val formattedNumber = decimalFormat.format(it)
////            textView.text = "$formattedNumber UZS"
//        }
//    }
//}



class TotalPriceObserver(private val textView: TextView) : CartObserver {
    override fun onCartChanged(totalPrice: Number, totalSalePrice: Number, totalCount: Int) {
        textView.text = "Общая сумма: $totalPrice"
    }
}

class TotalSalePriceObserver(private val textView: TextView) : CartObserver {
    override fun onCartChanged(totalPrice: Number, totalSalePrice: Number, totalCount: Int) {
        textView.text = "Сумма скидок: $totalSalePrice"
    }
}

class TotalCountObserver(private val textView: TextView) : CartObserver {
    override fun onCartChanged(totalPrice: Number, totalSalePrice: Number, totalCount: Int) {
        textView.text = "Количество товаров: $totalCount"
    }
}
