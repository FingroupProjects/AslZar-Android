@file:Suppress("NAME_SHADOWING")

package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.fin_group.aslzar.R
import com.fin_group.aslzar.cart.Cart
import com.fin_group.aslzar.databinding.FragmentCalculatorBinding
import com.fin_group.aslzar.models.AllClientType
import com.fin_group.aslzar.models.AllTypePay
import com.fin_group.aslzar.models.Installment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragment
import com.fin_group.aslzar.util.formatNumber
import okhttp3.internal.format

fun CalculatorFragment.fetViews(binding: FragmentCalculatorBinding) {

    typeClient = binding.spinnerClientType
    typePay = binding.spinnerPayType
    checkBox = binding.checkbox
    firstPayCalculator = binding.firstPayCalculator
    checkboxForBonus = binding.checkboxForBonus
    bonus = binding.bonus
    tableSale = binding.tableSale
    firstPay = binding.tvFirstPay
    sale = binding.tvSale
    payWithBonus = binding.tvPayWithBonus
    tvFirstPayCalculator = binding.tvFirstPayCalculator
    summa = binding.summa
    tvBonusForClient = binding.tvBonusForClient
    editBonus = binding.editBonus
    tvTable = binding.tvTable
    monthTable = binding.monthTable
    percentTable = binding.percentTable
    tvSale = binding.tvSale
    getInstallment = listOf()
}

@SuppressLint("SetTextI18n")
fun CalculatorFragment.calculator() {

    val getTotalCart = Cart.getTotalPrice()
    summa.text = "${formatNumber(getTotalCart)} UZS"

    val getTotalWithSale = Cart.getTotalPriceWithSale()
    tvSale.text = "${formatNumber(getTotalWithSale)} UZS"

    val allClientType = listOf(
        AllClientType(1, "Розничный", 0),
        AllClientType(2, "Шарифов Тохир", 200000),
        AllClientType(3, "Шахобов Нуриддин", 300000),
        AllClientType(4, "Гафуров Сухроб", 400000)
    )

    val allTypePay = listOf(
        AllTypePay(1, "Наличными"),
        AllTypePay(2, "В рассрочку")
    )

    val arrayAdapterTypeClient = ArrayAdapter(requireContext(), R.layout.spinner_item, allClientType.map { it.name })
    typeClient.setAdapter(arrayAdapterTypeClient)

    val arrayAdapterTypePay = ArrayAdapter(requireContext(), R.layout.spinner_item, allTypePay.map { it.name })
    typePay.setAdapter(arrayAdapterTypePay)

    typeClient.setOnItemClickListener { _, _, position, _ ->
        val selectClientType = allClientType[position]
        val selectClientTypeId = selectClientType.id

        if (selectClientTypeId == 1) {
            val selectedClientBonus = selectClientType.bonus
            tvBonusForClient.text = "${formatNumber(selectedClientBonus)} UZS"

            payWithBonus.text = "${formatNumber(0.00)} UZS"

            checkboxForBonus.visibility = View.GONE
            bonus.visibility = View.GONE
            checkBox.visibility = View.GONE
            firstPayCalculator.visibility = View.GONE
            tableSale.visibility = View.GONE
            tvTable.visibility = View.GONE
            checkBox.isChecked = false
            bonus.editText?.setText("")

            typePay.setOnItemClickListener { _, _, position, _ ->
                val selectPayType = allTypePay[position]
                val selectPayTypeId = selectPayType.id

                if (selectPayTypeId == 1) {
                    checkboxForBonus.visibility = View.GONE
                    bonus.visibility = View.GONE
                    checkBox.visibility = View.GONE
                    firstPayCalculator.visibility = View.GONE
                    tableSale.visibility = View.GONE
                    tvTable.visibility = View.GONE
                    checkBox.isChecked = false
                } else {
                    checkboxForBonus.visibility = View.GONE
                    bonus.visibility = View.GONE
                    checkBox.visibility = View.VISIBLE

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            firstPayCalculator.visibility = View.VISIBLE
                            tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                        } else {
                            firstPayCalculator.visibility = View.GONE
                            firstPayCalculator.editText?.setText("")
                            tvFirstPayCalculator.keyListener = null
                        }
                    }
                    tableSale.visibility = View.VISIBLE
                    tvTable.visibility = View.VISIBLE
                    firstPayCalculator.editText?.addTextChangedListener {
                        val inputFirstPayCalculator = it.toString()
                        if (inputFirstPayCalculator.isNotEmpty()) {
                            firstPay.text = "${formatNumber(inputFirstPayCalculator.toDouble())}  UZS"
                        } else {
                            firstPay.text = "0 UZS"
                        }
                    }
                }
            }
        } else {

            val selectedClientBonus = selectClientType.bonus
            tvBonusForClient.text = "${formatNumber(selectedClientBonus)} UZS"

            bonus.editText?.setText("")
            editBonus.keyListener = null
            checkboxForBonus.isChecked = false

            checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    bonus.visibility = View.VISIBLE
                    editBonus.inputType = InputType.TYPE_CLASS_NUMBER
                } else {
                    bonus.visibility = View.GONE
                    bonus.editText?.setText("")
                    editBonus.keyListener = null
                }
            }
            checkboxForBonus.visibility = View.VISIBLE
            bonus.visibility = View.GONE
            checkBox.visibility = View.GONE
            firstPayCalculator.visibility = View.GONE
            tableSale.visibility = View.GONE
            tvTable.visibility = View.GONE
            typePay.setOnItemClickListener { _, _, position, _ ->
                val selectPayType = allTypePay[position]
                val selectPayTypeId = selectPayType.id
                if (selectPayTypeId == 1) {
                    bonus.editText?.setText("")
                    checkboxForBonus.visibility = View.VISIBLE
                    checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            bonus.visibility = View.VISIBLE
                            editBonus.inputType = InputType.TYPE_CLASS_NUMBER
                        } else {
                            bonus.visibility = View.GONE
                            bonus.editText?.setText("")
                            editBonus.keyListener = null
                        }
                    }
                    bonus.editText?.addTextChangedListener {
                        val inputBonus = it.toString()
                        if (inputBonus.isNotEmpty()) {
                            payWithBonus.text = "${formatNumber(inputBonus.toDouble())} UZS"
                        } else {
                            payWithBonus.text = "0 UZS"
                        }
                    }
                    checkBox.visibility = View.GONE
                    firstPayCalculator.visibility = View.GONE
                    tableSale.visibility = View.GONE
                    tvTable.visibility = View.GONE
                    checkBox.isChecked = false
                } else {

                    checkboxForBonus.isChecked = false
                    checkBox.isChecked = false

                    checkboxForBonus.visibility = View.VISIBLE
                    checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            bonus.visibility = View.VISIBLE
                            editBonus.inputType = InputType.TYPE_CLASS_NUMBER
                        } else {
                            bonus.visibility = View.GONE
                            bonus.editText?.setText("")
                            editBonus.keyListener = null
                        }
                    }
                    bonus.editText?.addTextChangedListener {
                        val inputBonus = it.toString()
                        if (inputBonus.isNotEmpty()) {
                            payWithBonus.text = "${formatNumber(inputBonus.toDouble())} UZS"
                        } else {
                            payWithBonus.text = "0 UZS"
                        }
                    }
                    checkBox.visibility = View.VISIBLE
                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            firstPayCalculator.visibility = View.VISIBLE
                            tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                        } else {
                            firstPayCalculator.visibility = View.GONE
                            firstPayCalculator.editText?.setText("")
                            tvFirstPayCalculator.keyListener = null
                        }
                    }
                    firstPayCalculator.editText?.addTextChangedListener {
                        val inputFirstPay = it.toString()
                        if (inputFirstPay.isNotEmpty()) {
                            firstPay.text = "${formatNumber(inputFirstPay.toDouble())} UZS"
                        } else {
                            firstPay.text = "0 UZS"
                        }
                    }
                    tableSale.visibility = View.VISIBLE
                    tvTable.visibility = View.VISIBLE
                }
            }
        }
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragment.createTable() {

    val totalCart = Cart.getTotalPrice()
    val getTotalWithSale = Cart.getTotalPriceWithSale()
    tvSale.text = "${formatNumber(getTotalWithSale)} UZS"

    getInstallment = listOf(
        Installment("2", 20),
        Installment("3", 30),
        Installment("4", 40),
        Installment("5", 50),
        Installment("6", 60),
        Installment("7", 70),
        Installment("8", 80),
        Installment("9", 90)
    )

    val styleTextColor = ContextCompat.getColor(requireContext(), R.color.text_color_1)
    val styleTextSize = 15f
    val styleBackground = R.drawable.bg_text_view_in_table
    val styleTextAlignment = TextView.TEXT_ALIGNMENT_CENTER
    val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)

    for (i in getInstallment.indices) {

        // Create month table
        val monthTextView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            text = "${getInstallment[i].month} платежей"
            gravity = Gravity.CENTER
            setTextColor(styleTextColor)
            textSize = styleTextSize
            if (i < getInstallment.size - 1) {
                setBackgroundResource(styleBackground)
            }
            textAlignment = styleTextAlignment
            typeface = boldTypeface
        }
        monthTable.addView(monthTextView)

        //Расчет без первоначльного взноса
        val getMonth = getInstallment[i].month
        val getPercent = getInstallment[i].percent
        val getPercentFromTotalCart = (totalCart.toDouble() * getPercent.toDouble()) / 100
        val plusPercent = totalCart.toDouble() + getPercentFromTotalCart
        val tablePercent = plusPercent / getMonth.toInt()

        // Create pay table
        val amountTextView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            getInstallment[i].percent.toString()

            // Расчет с первональным взносом
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER

                } else {
                    firstPayCalculator.visibility = View.GONE
                    firstPayCalculator.editText?.setText("")
                    tvFirstPayCalculator.keyListener = null
                    summa.text = "${formatNumber(totalCart)} UZS"
                    text = formatNumber(tablePercent)

                }
            }

            firstPayCalculator.editText?.addTextChangedListener {
                val inputFirstPay = it.toString()
                if (inputFirstPay.isNotEmpty()) {
                    val getFirstPay = inputFirstPay

                    val realPercent = totalCart.toDouble() / 2

                    if (getFirstPay.toDouble() > realPercent){
                        firstPayCalculator.error = "Первоначальный взнос должен составлять не менее 50% от суммы покупки!"
                        firstPay.text = "0,00 UZS"
                    }else{
                        val getTotalCart = totalCart.toDouble() - getFirstPay.toDouble()
                        summa.text = "${formatNumber(getTotalCart)} UZS"
                        val minusFirstPay = totalCart.toDouble() - getFirstPay.toDouble()
                        val getPercentFromTotalCart = (minusFirstPay * getPercent.toInt()) / 100
                        val plusPercent = minusFirstPay + getPercentFromTotalCart
                        val tablePercentWithFirstPay = plusPercent / getMonth.toInt()
                        text = formatNumber(tablePercentWithFirstPay)
                    }
                }else{
                    summa.text = "${formatNumber(totalCart)} UZS"
                    text = formatNumber(tablePercent)
                }
            }

            //Расчет с бонусом
            bonus.editText?.addTextChangedListener {
                val inputBonus = it.toString()
                if (inputBonus.isNotEmpty()) {
                    val getPayWithBonus = inputBonus
                    val getTotalCart = totalCart.toDouble() - getPayWithBonus.toDouble()
                    summa.text = "${formatNumber(getTotalCart)} UZS"
                    val minusBonus = totalCart.toDouble() - getPayWithBonus.toDouble()
                    val getPercentFromTotalCart = (minusBonus * getPercent.toInt()) / 100
                    val plusPercent = minusBonus + getPercentFromTotalCart
                    val tablePercentWithFirstPay = plusPercent / getMonth.toInt()
                    text = formatNumber(tablePercentWithFirstPay)
                    payWithBonus.text = "${formatNumber(getPayWithBonus.toDouble())} UZS"
                } else {
                    payWithBonus.text = "0 UZS"
                    summa.text = "${formatNumber(totalCart)} UZS"
                }
            }

            //Расчет с учетом бонуса и первоначального взноса
            bonus.editText?.addTextChangedListener {
                val inputBonus = it.toString()
                if (inputBonus.isNotEmpty()) {
                    firstPayCalculator.editText?.addTextChangedListener {
                        val inputFirstPay = it.toString()
                        if (inputFirstPay.isNotEmpty()) {
                            val getSummaTotalCart = inputBonus.toDouble() + inputFirstPay.toDouble()
                            val getTotalCart = totalCart.toDouble() - getSummaTotalCart
                            summa.text = "${formatNumber(getTotalCart)} UZS"
                            val total = inputBonus.toDouble() + inputFirstPay.toDouble()
                            val totalWithOutBonusAndFirstPay = totalCart.toDouble() - total
                            val getPercentFromTotalCart = (totalWithOutBonusAndFirstPay * getPercent.toInt()) / 100
                            val plusPercent = totalWithOutBonusAndFirstPay + getPercentFromTotalCart
                            val tablePercentWithFirstPay = plusPercent / getMonth.toInt()
                            text = formatNumber(tablePercentWithFirstPay)

                        }else{
                            tvSale.text = "0 UZS"
                            firstPay.text = "0 UZS"
                            summa.text = "${formatNumber(totalCart)} UZS"
                        }
                    }
                }
            }
            text = formatNumber(tablePercent)
            gravity = Gravity.CENTER
            setTextColor(styleTextColor)
            textSize = styleTextSize
            if (i < getInstallment.size - 1) {
                setBackgroundResource(styleBackground)
            }
            textAlignment = styleTextAlignment
            typeface = boldTypeface
        }
        percentTable.addView(amountTextView)
    }
}