@file:Suppress("NAME_SHADOWING")

package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import com.fin_group.aslzar.R
import com.fin_group.aslzar.databinding.FragmentCalculatorBinding
import com.fin_group.aslzar.models.AllClientType
import com.fin_group.aslzar.models.AllTypePay
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragment


fun CalculatorFragment.fetViews(binding: FragmentCalculatorBinding) {
    typeClient = binding.spinnerClientType
    typePay = binding.spinnerPayType
    checkBox = binding.checkbox
    firstPayCalculator = binding.firstPayCalculator
    checkboxForBonus = binding.checkboxForBonus
    bonus = binding.bonus
    tableSale = binding.tableSale
    tvTableSale = binding.tvTableSale


    firstPay = binding.tvFirstPay
    sale = binding.tvSale
    payWithBonus = binding.tvPayWithBonus

    tvFirstPayCalculator = binding.tvFirstPayCalculator

    summa = binding.summa

    tvBonusForClient = binding.tvBonusForClient

    editBonus = binding.editBonus

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
            tvTableSale.visibility = View.GONE
            checkBox.isChecked = true
            bonus.editText?.setText("")
            


            typePay.setOnItemClickListener { _, _, position, _ ->
                val selectPayType = allTypePay[position]
                val selectPayTypeId = selectPayType.id

                if (selectPayTypeId == 1){
                    checkboxForBonus.visibility = View.GONE
                    bonus.visibility = View.GONE
                    checkBox.visibility = View.GONE
                    firstPayCalculator.visibility = View.GONE
                    tableSale.visibility = View.GONE
                    tvTableSale.visibility = View.GONE
                    checkBox.isChecked = true
                }else{
                    checkboxForBonus.visibility = View.GONE
                    bonus.visibility = View.GONE
                    checkBox.visibility = View.VISIBLE

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        firstPayCalculator.visibility = View.GONE
                        firstPayCalculator.editText?.setText("")
                        tvFirstPayCalculator.keyListener = null
                    } else {
                        firstPayCalculator.visibility = View.VISIBLE
                        tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                }
                    tableSale.visibility = View.VISIBLE
                    tvTableSale.visibility = View.VISIBLE
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
        }else{

            val selectedClientBonus = "${selectClientType.bonus} UZS"
            tvBonusForClient.text = selectedClientBonus

            bonus.editText?.setText("")
            editBonus.keyListener = null
            checkboxForBonus.isChecked = true


            checkboxForBonus.visibility = View.VISIBLE
            bonus.visibility = View.GONE
            checkBox.visibility = View.GONE
            firstPayCalculator.visibility = View.GONE
            tableSale.visibility = View.GONE
            tvTableSale.visibility = View.GONE
            typePay.setOnItemClickListener { _, _, position, _ ->
                val selectPayType = allTypePay[position]
                val selectPayTypeId = selectPayType.id
                if (selectPayTypeId == 1){
                    bonus.editText?.setText("")
                    checkboxForBonus.visibility = View.VISIBLE
                    checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            bonus.visibility = View.GONE
                            bonus.editText?.setText("")
                            editBonus.keyListener = null
                        } else {
                            bonus.visibility = View.VISIBLE
                            editBonus.inputType = InputType.TYPE_CLASS_NUMBER
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
                    tvTableSale.visibility = View.GONE
                    checkBox.isChecked = true
                }else{

                    checkboxForBonus.isChecked = true
                    checkBox.isChecked = true

                    checkboxForBonus.visibility = View.VISIBLE
                    checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            bonus.visibility = View.GONE
                            bonus.editText?.setText("")
                            editBonus.keyListener = null
                        } else {
                            bonus.visibility = View.VISIBLE
                            editBonus.inputType = InputType.TYPE_CLASS_NUMBER
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
                            firstPayCalculator.visibility = View.GONE
                            firstPayCalculator.editText?.setText("")
                            tvFirstPayCalculator.keyListener = null
                        } else {
                            firstPayCalculator.visibility = View.VISIBLE
                            tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
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



















                    tableSale.visibility = View.GONE
                    tvTableSale.visibility = View.GONE

















                }
            }
        }

    }
}