@file:Suppress("NAME_SHADOWING")

package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.fin_group.aslzar.R
import com.fin_group.aslzar.models.AllClientType
import com.fin_group.aslzar.models.AllTypePay
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout

@SuppressLint("SetTextI18n")
fun CalculatorFragment.calculator() {
    val allClientType = listOf(
        AllClientType(1, "Розничный"),
        AllClientType(2, "Шарифов Тохир"),
        AllClientType(3, "Шахобов Нуриддин"),
        AllClientType(4, "Гафуров Сухроб")
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

        typePay.setOnItemClickListener { _, _, position, _ ->
            val selectTypePay = allTypePay[position]
            val selectTypePayId = selectTypePay.id

            if (selectClientTypeId == 1 && selectTypePayId == 1){
                checkBox.visibility = View.GONE
                firstPayCalculator.visibility = View.GONE
                checkboxForBonus.visibility = View.GONE
                bonus.visibility = View.GONE
                tableSale.visibility = View.GONE
                tvTableSale.visibility = View.GONE
            }else if (selectClientTypeId == 1 && selectTypePayId == 2){
                checkBox.visibility = View.VISIBLE
                firstPayCalculator.visibility = View.VISIBLE

                firstPayCalculator.editText?.addTextChangedListener {
                    val inputText = it.toString()
                    if (inputText.isNotEmpty()) {
                        firstPay.text = "$inputText UZS"
                    } else {
                        firstPay.text = "0 UZS"
                    }
                }

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        firstPayCalculator.visibility = View.GONE
                        tvFirstPayCalculator.inputType = android.text.InputType.TYPE_NULL

                        val tvFirstPayCalculator = firstPayCalculator.editText
                        val text = tvFirstPayCalculator?.setText("")

                        firstPayCalculator.editText?.addTextChangedListener {
                            firstPay.text = "$text UZS"

                        }



















                    } else {
                        firstPayCalculator.visibility = View.VISIBLE
                    }
                }
                checkboxForBonus.visibility = View.GONE
                bonus.visibility = View.GONE
                tableSale.visibility = View.VISIBLE
                tvTableSale.visibility = View.VISIBLE
            } else if (selectClientTypeId != 1 && selectTypePayId == 1){
                checkBox.visibility = View.GONE
                firstPayCalculator.visibility = View.GONE
                checkboxForBonus.visibility = View.VISIBLE
                bonus.visibility = View.VISIBLE
                checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked) {
                        bonus.visibility = View.VISIBLE
                    } else {
                        bonus.visibility = View.GONE
                    }
                }
                tableSale.visibility = View.GONE
                tvTableSale.visibility = View.GONE
            }else if (selectClientTypeId != 1 && selectTypePayId == 2){
                checkBox.visibility = View.VISIBLE
                firstPayCalculator.visibility = View.VISIBLE
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked) {
                        firstPayCalculator.visibility = View.VISIBLE
                    } else {
                        firstPayCalculator.visibility = View.GONE
                    }
                }
                checkboxForBonus.visibility = View.VISIBLE
                bonus.visibility = View.VISIBLE
                checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked) {
                        bonus.visibility = View.VISIBLE
                    } else {
                        bonus.visibility = View.GONE
                    }
                }
                tableSale.visibility = View.VISIBLE
                tvTableSale.visibility = View.VISIBLE
            }
        }
    }
}



