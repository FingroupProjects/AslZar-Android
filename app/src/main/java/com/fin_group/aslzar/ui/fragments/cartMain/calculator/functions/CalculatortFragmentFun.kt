@file:Suppress("NAME_SHADOWING")

package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.fin_group.aslzar.R
import com.fin_group.aslzar.models.AllClientType
import com.fin_group.aslzar.models.AllTypePay
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout

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
        if (selectClientTypeId == 1){
            typePay.setOnItemClickListener { _, _, position, _ ->
                val selectPayType = allTypePay[position]
                val selectPayTypeId = selectPayType.id
                if (selectPayTypeId == 1) {
                    checkboxForBonus.visibility = View.GONE
                    bonus.visibility = View.GONE
                    checkBox.visibility = View.GONE
                    firstPayCalculator.visibility = View.GONE
                    tableSale.visibility = View.GONE
                    tvTableSale.visibility = View.GONE
                } else {
                    checkBox.visibility = View.VISIBLE
                    firstPayCalculator.visibility = View.VISIBLE
                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        if (!isChecked) {
                            firstPayCalculator.visibility = View.VISIBLE
                        } else {
                            firstPayCalculator.visibility = View.GONE
                        }
                    }
                    tableSale.visibility = View.VISIBLE
                    tvTableSale.visibility = View.VISIBLE
                }
            }

        }else{
            typePay.setOnItemClickListener { _, _, position, _ ->
                val selectPayType = allTypePay[position]
                val selectPayTypeId = selectPayType.id
                if (selectPayTypeId == 1) {
                    checkboxForBonus.visibility = View.VISIBLE
                    bonus.visibility = View.VISIBLE

                    checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                        if (!isChecked) {
                            bonus.visibility = View.VISIBLE
                        } else {
                            bonus.visibility = View.GONE
                        }
                    }
                    checkBox.visibility = View.GONE
                    firstPayCalculator.visibility = View.GONE
                    tableSale.visibility = View.GONE
                    tvTableSale.visibility = View.GONE

                } else {
                    checkboxForBonus.visibility = View.VISIBLE
                    bonus.visibility = View.VISIBLE

                    checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                        if (!isChecked) {
                            bonus.visibility = View.VISIBLE
                        } else {
                            bonus.visibility = View.GONE
                        }
                    }
                    checkBox.visibility = View.VISIBLE
                    firstPayCalculator.visibility = View.VISIBLE

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        if (!isChecked) {
                            firstPayCalculator.visibility = View.VISIBLE
                        } else {
                            firstPayCalculator.visibility = View.GONE
                        }
                    }

                    tableSale.visibility = View.VISIBLE
                    tvTableSale.visibility = View.VISIBLE
                }
            }




        }



    }




}



