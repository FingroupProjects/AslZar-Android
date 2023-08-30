@file:Suppress("NAME_SHADOWING")

package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentCalculatorBinding
import com.fin_group.aslzar.models.AllClientType
import com.fin_group.aslzar.models.AllTypePay
import com.fin_group.aslzar.models.Installment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragment

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

}

@SuppressLint("SetTextI18n")
fun CalculatorFragment.calculator() {
    val allClientType = listOf(
        AllClientType(1, "Розничный", 0),
        AllClientType(2, "Шарифов Тохир", 50),
        AllClientType(3, "Шахобов Нуриддин", 100),
        AllClientType(4, "Гафуров Сухроб", 200)
    )

    val allTypePay = listOf(
        AllTypePay(1, "Наличными"),
        AllTypePay(2, "В рассрочку")
    )

    val arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, allClientType.map { it.name })
    typeClient.setAdapter(arrayAdapterTypeClient)

    val arrayAdapterTypePay =
        ArrayAdapter(requireContext(), R.layout.spinner_item, allTypePay.map { it.name })
    typePay.setAdapter(arrayAdapterTypePay)

    typeClient.setOnItemClickListener { _, _, position, _ ->
        val selectClientType = allClientType[position]
        val selectClientTypeId = selectClientType.id

        if (selectClientTypeId == 1) {
            val selectedClientBonus = "${selectClientType.bonus} UZS"
            tvBonusForClient.text = selectedClientBonus

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
                            firstPay.text = "$inputFirstPayCalculator UZS"
                        } else {
                            firstPay.text = "0 UZS"
                        }
                    }
                }
            }
        } else {

            val selectedClientBonus = "${selectClientType.bonus} UZS"
            tvBonusForClient.text = selectedClientBonus

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
                            payWithBonus.text = "$inputBonus UZS"
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
                            payWithBonus.text = "$inputBonus UZS"
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
                            firstPay.text = "$inputFirstPay UZS"
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

    val getInstallment = listOf(
        Installment("2", 2),
        Installment("3", 3),
        Installment("4", 4),
        Installment("5", 5),
        Installment("6", 6),
        Installment("7", 7),
//        Installment("8", "800"),
//        Installment("9", "900"),
//        Installment("10", "1000"),
//        Installment("11", "1100"),
//        Installment("12", "1200"),
//        Installment("20", "2000"),
//        Installment("22", "2200")
    )

    val styleTextColor = ContextCompat.getColor(requireContext(), R.color.text_color_1)
    val styleTextSize = 15f
    val styleBackground = R.drawable.bg_text_view_in_table
    val styleTextAlignment = TextView.TEXT_ALIGNMENT_CENTER
    val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)


    for (i in getInstallment.indices) {
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

        val amountTextView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            text = getInstallment[i].percent.toString()
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
