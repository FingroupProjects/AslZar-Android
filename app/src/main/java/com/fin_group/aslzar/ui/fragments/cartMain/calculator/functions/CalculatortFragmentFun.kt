@file:Suppress("NAME_SHADOWING")

package com.fin_group.aslzar.ui.fragments.cartMain.calculator.functions

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.fin_group.aslzar.response.Client
import com.fin_group.aslzar.response.GetAllClientsResponse
import com.fin_group.aslzar.response.Percent
import com.fin_group.aslzar.response.PercentInstallment
import com.fin_group.aslzar.ui.fragments.cartMain.calculator.CalculatorFragment
import com.fin_group.aslzar.util.CartObserver
import com.fin_group.aslzar.util.formatNumber
import com.google.android.gms.common.api.GoogleApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun CalculatorFragment.fetViews(binding: FragmentCalculatorBinding) {
    tvBonusForClient = binding.tvBonusForClient
    firstPay = binding.tvFirstPay
    tvSale = binding.tvSale
    payWithBonus = binding.tvPayWithBonus
    summa = binding.summa
    typePay = binding.spinnerPayType
    checkBox = binding.checkbox
    firstPayCalculator = binding.firstPayCalculator
    checkboxForBonus = binding.checkboxForBonus
    tvFirstPayCalculator = binding.tvFirstPayCalculator
    bonus = binding.bonus
    editBonus = binding.editBonus
    tvTable = binding.tvTable
    monthTable = binding.monthTable
    percentTable = binding.percentTable
    tableSale = binding.tableSale
    spinnerClientType = binding.spinnerClientType
    getAllClient = listOf()
    getPercentAndMonth = listOf()
}

fun CalculatorFragment.cartObserver(binding: FragmentCalculatorBinding) {
    val totalPriceWithSaleTv = binding.tvSale
    val totalPriceTv = binding.summa

    cartObserver = object : CartObserver {
        @SuppressLint("SetTextI18n")
        override fun onCartChanged(
            totalPriceWithoutSale: Number,
            totalPriceWithSale: Number,
            totalCount: Int,
            totalPrice: Number
        ) {
            totalPriceWithSaleTv.text = "${formatNumber(totalPriceWithSale)} UZS"
            totalPriceTv.text = "${formatNumber(totalPrice)} UZS"
            totalCart = totalPrice
        }
    }
}

@SuppressLint("SetTextI18n")
fun CalculatorFragment.createTable() {

    val totalCart = Cart.getTotalPrice()

    val getInstallment = listOf(
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

        // CREATE MONTH TABLE
        val monthTextView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
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
        val getMonth = getInstallment[i].month.toDouble()
        Log.d("TAG", "get month: $getMonth")
        val getPercent = getInstallment[i].percent.toDouble()
        Log.d("Tag", "get percent: $getPercent")
        val getPercentFromTotalCart = (totalCart.toDouble() * getPercent) / 100
        val plusPercent = totalCart.toDouble() + getPercentFromTotalCart
        val tablePercent = plusPercent / getMonth

        // CREATE PERCENT TABLE
        val amountTextView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            firstPayCalculator.editText?.addTextChangedListener {
                val inputFirstPay = it.toString()
                if (inputFirstPay.isNotEmpty()) {
                    val getFirstPay = inputFirstPay
                    firstPayCalculator.error = null
                    val minusFirstPay = totalCart.toDouble() - getFirstPay.toDouble()
                    val getPercentFromTotalCart = (minusFirstPay * getPercent.toInt()) / 100
                    val plusPercent = minusFirstPay + getPercentFromTotalCart
                    val tablePercentWithFirstPay = plusPercent / getMonth
                    text = formatNumber(tablePercentWithFirstPay)
                }
            }

            //Расчет с бонусом
            bonus.editText?.addTextChangedListener {
                val inputBonus = it.toString()
                if (inputBonus.isNotEmpty()) {
                    val getPayWithBonus = inputBonus
                    val minusBonus = totalCart.toDouble() - getPayWithBonus.toDouble()
                    val getPercentFromTotalCart = (minusBonus * getPercent.toInt()) / 100
                    val plusPercent = minusBonus + getPercentFromTotalCart
                    val tablePercentWithFirstPay = plusPercent / getMonth
                    text = formatNumber(tablePercentWithFirstPay)
                }
            }

            //Расчет с учетом бонуса и первоначального взноса
            bonus.editText?.addTextChangedListener {
                val inputBonus = it.toString()
                if (inputBonus.isNotEmpty()) {
                    firstPayCalculator.editText?.addTextChangedListener {
                        val inputFirstPay = it.toString()
                        if (inputFirstPay.isNotEmpty()) {
                            val minusBonus = totalCart.toDouble() - inputBonus.toDouble()
                            summa.text = "${formatNumber(minusBonus)} UZS"
                            val total = minusBonus - inputFirstPay.toDouble()
                            summa.text = "${formatNumber(total)} UZS"
                            val getPercentFromTotalCart = (total * getPercent.toInt()) / 100
                            val plusPercent = total + getPercentFromTotalCart
                            val tablePercentWithFirstPay = plusPercent / getMonth
                            text = formatNumber(tablePercentWithFirstPay)
                        }else{
                            val minusBonus = totalCart.toDouble() - inputBonus.toDouble()
                            val getPercentFromTotalCart = (minusBonus * getPercent.toInt()) / 100
                            val plusPercent = minusBonus + getPercentFromTotalCart
                            val tablePercentWithFirstPay = plusPercent / getMonth
                            text = formatNumber(tablePercentWithFirstPay)
                            summa.text = "${formatNumber(minusBonus)} UZS"
                        }
                    }
                }else{
                    val inputFirstPay = it.toString()
                    if (inputFirstPay.isNotEmpty()) {
                        val minusFirstPay = totalCart.toDouble() - inputFirstPay.toDouble()
                        val getPercentFromTotalCart = (minusFirstPay * getPercent.toInt()) / 100
                        val plusPercent = minusFirstPay + getPercentFromTotalCart
                        val tablePercentWithFirstPay = plusPercent / getMonth
                        text = formatNumber(tablePercentWithFirstPay)
                        summa.text = "${formatNumber(minusFirstPay)} UZS"
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

@SuppressLint("SetTextI18n")
fun CalculatorFragment.all() {
    val totalCart = Cart.getTotalPrice().toDouble()
    summa.text = "${formatNumber(totalCart)} UZS"

    val allTypePay = listOf(
        AllTypePay(1, "Наличными"),
        AllTypePay(2, "В рассрочку")
    )


    val arrayAdapterTypeClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, getAllClient.map { it.client_name })
    spinnerClientType.setAdapter(arrayAdapterTypeClient)

    val arrayAdapterTypePay =
        ArrayAdapter(requireContext(), R.layout.spinner_item, allTypePay.map { it.name })
    typePay.setAdapter(arrayAdapterTypePay)

    spinnerClientType.setOnItemClickListener { _, _, position, _ ->
        val selectClientType = getAllClient[position]

        val autoCompleteTextView = typePay.findViewById<AutoCompleteTextView>(R.id.spinnerPayType)

        val selectedPayType = allTypePay.find { it.name == autoCompleteTextView.text.toString() }

        if (selectedPayType != null) {
            if (selectClientType.client_name.contains("розничный покупатель", true)) {
                handleRetailClient(selectedPayType, totalCart)
            } else {
                handleNonRetailClient(selectClientType, selectedPayType, totalCart)
            }
        }
    }
}

@SuppressLint("SetTextI18n")
private fun CalculatorFragment.handleRetailClient(selectedPayType: AllTypePay, totalCart: Double) {

    if (selectedPayType.id == 1) {
        // Розничные клиенты, которые платят наличными

        tvBonusForClient.text = "${formatNumber(0.00)} UZS"
        firstPay.text = "${formatNumber(0.00)} UZS"
        tvSale.text = "${formatNumber(0.00)} UZS"
        payWithBonus.text = "${formatNumber(0.00)} UZS"
        summa.text = "${formatNumber(totalCart)} UZS"

        checkboxForBonus.visibility = View.GONE
        checkboxForBonus.isChecked = false

        bonus.visibility = View.GONE
        bonus.editText?.setText("")

        editBonus.keyListener = null

        checkBox.visibility = View.GONE
        checkBox.isChecked = false

        firstPayCalculator.visibility = View.GONE
        firstPayCalculator.editText?.setText("")

        tvFirstPayCalculator.keyListener = null

        tableSale.visibility = View.GONE
        tvTable.visibility = View.GONE


    } else if (selectedPayType.id == 2) {
        // Розничные клиенты, которые платят в рассрочку

        tvBonusForClient.text = "${formatNumber(0.00)} UZS"
        firstPay.text = "${formatNumber(0.00)} UZS"
        tvSale.text = "${formatNumber(0.00)} UZS"
        payWithBonus.text = "${formatNumber(0.00)} UZS"
        summa.text = "${formatNumber(totalCart)} UZS"

        checkboxForBonus.visibility = View.GONE
        checkboxForBonus.isChecked = false

        bonus.visibility = View.GONE
        bonus.editText?.setText("")

        editBonus.keyListener = null

        checkBox.visibility = View.VISIBLE
        checkBox.isChecked = false
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                firstPayCalculator.visibility = View.VISIBLE
                firstPayCalculator.editText?.addTextChangedListener {
                    val getTextFirstPayCalculator = it.toString()
                    if (getTextFirstPayCalculator.isNotEmpty()) {
                        val toNumber = getTextFirstPayCalculator.toDouble()
                        Log.d("TAG", "Первоначальный взнос: $toNumber")
                        val getPercent = (totalCart * 15) / 100
                        Log.d("TAG", "15% от первоначального взноса: $getPercent")
                        if (toNumber in getPercent..totalCart) {
                            firstPay.text = "${formatNumber(toNumber)} UZS"
                            val minusFirstPay = totalCart - toNumber
                            Log.d("TAG", "Минус первоначального взноса от суммы: $minusFirstPay")
                            summa.text = "${formatNumber(minusFirstPay)} UZS"
                            tableSale.visibility = View.VISIBLE
                            tvTable.visibility = View.VISIBLE
                        } else if (toNumber < getPercent){
                            firstPayCalculator.error = "Первоначальный взнос должен составлять не менее 15% от суммы покупки!"
                            tableSale.visibility = View.GONE
                            tvTable.visibility = View.GONE
                        }
                        else if (toNumber > totalCart){
                            firstPayCalculator.error = "Первоначальный взнос не должен превышать сумму покупки!"
                            tableSale.visibility = View.GONE
                            tvTable.visibility = View.GONE
                        }
                    } else {
                        firstPayCalculator.error = null
                        firstPay.text = "${formatNumber(0.00)} UZS"
                        summa.text = "${formatNumber(totalCart)} UZS"
                        tableSale.visibility = View.VISIBLE
                        tvTable.visibility = View.VISIBLE
                    }
                }
            } else {
                firstPayCalculator.visibility = View.GONE
                firstPayCalculator.editText?.setText("")
                tvFirstPayCalculator.keyListener = null
                firstPay.text = "${formatNumber(0.00)} UZS"
                summa.text = "${formatNumber(totalCart)} UZS"
            }
        }
        tableSale.visibility = View.VISIBLE
        tvTable.visibility = View.VISIBLE
    }
}

@SuppressLint("SetTextI18n")
private fun CalculatorFragment.handleNonRetailClient(
    selectedClientType: Client,
    selectedPayType: AllTypePay,
    totalCart: Double
) {

    if (selectedPayType.id == 1) {
        //Не-розничные клиенты, которые платят наличными

        val getBonusClient = selectedClientType.bonus.toDouble()

        tvBonusForClient.text = "${formatNumber(getBonusClient)} UZS"
        firstPay.text = "${formatNumber(0.00)} UZS"
        tvSale.text = "${formatNumber(0.00)} UZS"
        payWithBonus.text = "${formatNumber(0.00)} UZS"
        summa.text = "${formatNumber(totalCart)} UZS"

        checkboxForBonus.visibility = View.VISIBLE
        checkboxForBonus.isChecked = false
        checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editBonus.inputType = InputType.TYPE_CLASS_NUMBER
                bonus.visibility = View.VISIBLE
                bonus.editText?.addTextChangedListener {
                    val getTextBonus = it.toString()
                    if (getTextBonus.isNotEmpty()) {
                        val getNumber = getTextBonus.toDouble()
                        payWithBonus.text = "${formatNumber(getNumber)} UZS"
                        Log.d("TAG", "Бонус: $getNumber")
                        val minusBonus = totalCart - getNumber
                        Log.d("TAG", "Минус бонуса от суммы: $minusBonus")
                        summa.text = "${formatNumber(minusBonus)} UZS"
                    } else {
                        tvBonusForClient.text = "${formatNumber(0.00)} UZS"
                        summa.text = "${formatNumber(totalCart)} UZS"
                    }
                }
            } else {
                bonus.visibility = View.GONE
                bonus.editText?.setText("")
                editBonus.keyListener = null
                payWithBonus.text = "${formatNumber(0.00)} UZS"
                summa.text = "${formatNumber(totalCart)} UZS"
            }
        }

        checkBox.visibility = View.GONE
        checkBox.isChecked = false

        firstPayCalculator.visibility = View.GONE
        firstPayCalculator.editText?.setText("")

        tvFirstPayCalculator.keyListener = null

        tableSale.visibility = View.GONE
        tvTable.visibility = View.GONE

    } else if (selectedPayType.id == 2) {
        //Не-розничные клиенты, которые платят в рассрочку

        val getBonusClient = selectedClientType.bonus.toDouble()

        tvBonusForClient.text = "${formatNumber(getBonusClient)} UZS"
        firstPay.text = "${formatNumber(0.00)} UZS"
        tvSale.text = "${formatNumber(0.00)} UZS"
        payWithBonus.text = "${formatNumber(0.00)} UZS"
        summa.text = "${formatNumber(totalCart)} UZS"

        checkBox.visibility = View.GONE

        checkboxForBonus.visibility = View.VISIBLE
        checkboxForBonus.isChecked = false
        checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editBonus.inputType = InputType.TYPE_CLASS_NUMBER
                bonus.visibility = View.VISIBLE
                bonus.editText?.addTextChangedListener {
                    val getTextBonus = it.toString()
                    if (getTextBonus.isNotEmpty()) {
                        val getNumber = getTextBonus.toDouble()
                        payWithBonus.text = "${formatNumber(getNumber)} UZS"
                    } else {
                        payWithBonus.text = "${formatNumber(0.00)} UZS"
                    }
                }
            } else {
                bonus.visibility = View.GONE
                bonus.editText?.setText("")
                editBonus.keyListener = null
                tvBonusForClient.text = "${formatNumber(0.00)} UZS"
            }
        }

        checkBox.visibility = View.VISIBLE
        checkBox.isChecked = false
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                firstPayCalculator.visibility = View.VISIBLE
                firstPayCalculator.editText?.addTextChangedListener {
                    val getTextFirstPayCalculator = it.toString()
                    if (getTextFirstPayCalculator.isNotEmpty()) {
                        val toNumber = getTextFirstPayCalculator.toDouble()
                        Log.d("TAG", "Первоначальный взнос: $toNumber")
                        val getPercent = (totalCart * 15) / 100
                        Log.d("TAG", "15% от первоначального взноса: $getPercent")
                        if (toNumber >= getPercent) {
                            firstPay.text = "${formatNumber(toNumber)} UZS"
                        } else {
                            firstPayCalculator.error = "Первоначальный взнос должен составлять не менее 15% от суммы покупки!"
                        }
                    } else {
                        firstPay.text = "${formatNumber(0.00)} UZS"
                        firstPayCalculator.error = null
                    }
                }
            } else {
                firstPayCalculator.error = null
                firstPayCalculator.visibility = View.GONE
                firstPayCalculator.editText?.setText("")
                tvFirstPayCalculator.keyListener = null
                firstPay.text = "${formatNumber(0.00)} UZS"
            }
        }

        tableSale.visibility = View.VISIBLE
        tvTable.visibility = View.VISIBLE
    }
}


fun CalculatorFragment.getForPercentAndMonth(){
    try {
        val call = api.getApiService().getPercentAndMonth("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<PercentInstallment?>{
            override fun onResponse(
                call: Call<PercentInstallment?>,
                response: Response<PercentInstallment?>
            ) {
                Log.d("TAG", "1: ${response.code()}")
                Log.d("TAG", "2: ${response.body()}")
                if (response.isSuccessful){
                    val percentAndMonth = response.body()
                    if (percentAndMonth != null){
                        getPercentAndMonth = percentAndMonth.result
                        val monthAndPercent = Gson().toJson(getPercentAndMonth)
                        prefs.edit().putString("percentAndMonth", monthAndPercent).apply()
                    }
                }else{
                    Log.d("TAG", "onResponse: ${response.code()}")
                    Log.d("TAG", "onResponse: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<PercentInstallment?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }

        })
    }catch (e : Exception){
        Log.d("TAG", "getPercentAndMonth: ${e.message}")
    }
}

fun CalculatorFragment.fetchClientFromApi() {
    try {
        val call = api.getApiService().getAllClients("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<GetAllClientsResponse?> {
            override fun onResponse(
                call: Call<GetAllClientsResponse?>,
                response: Response<GetAllClientsResponse?>
            ) {
                if (response.isSuccessful) {
                    val clientName = response.body()
                    if (clientName != null) {
                        getAllClient = clientName.result
                        val nameClient = Gson().toJson(getAllClient)
                        prefs.edit().putString("clientName", nameClient).apply()
                        fetchClient(getAllClient)
                    }
                } else {
                    Log.d("TAG", "onResponse: ${response.code()}")
                    Log.d("TAG", "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetAllClientsResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    } catch (e: Exception) {
        Log.d("TAG", "fetchClientNameFromApi: ${e.message}")
    }
}

fun CalculatorFragment.fetchClient(client: List<Client>) {
    val arrayAdapterClient =
        ArrayAdapter(requireContext(), R.layout.spinner_item, client.map { it.client_name })
    spinnerClientType.setAdapter(arrayAdapterClient)

    spinnerClientType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

fun CalculatorFragment.fetchClientNameFromPrefs() {
    try {
        val clientNameJason = prefs.getString("clientName", null)
        if (clientNameJason != null) {
            val clientNameListType = object : TypeToken<List<Client>>() {}.type
            val nameClient = Gson().fromJson<List<Client>>(clientNameJason, clientNameListType)
            getAllClient = nameClient
            Log.d("TAG", "fetchNameClientFromPrefs: $nameClient")
            fetchClient(getAllClient)
        } else {
            fetchClientFromApi()
        }
    } catch (e: Exception) {
        Log.d("TAG", "fetchNameClientFromPrefs: ${e.message}")
    }
}