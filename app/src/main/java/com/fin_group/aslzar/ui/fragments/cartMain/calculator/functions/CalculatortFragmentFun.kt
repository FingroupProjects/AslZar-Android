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

//    getInstallment = listOf(
//        Installment("2", 20),
//        Installment("3", 30),
//        Installment("4", 40),
//        Installment("5", 50),
//        Installment("6", 60),
//        Installment("7", 70),
//        Installment("8", 80),
//        Installment("9", 90)
//    )

    val styleTextColor = ContextCompat.getColor(requireContext(), R.color.text_color_1)
    val styleTextSize = 15f
    val styleBackground = R.drawable.bg_text_view_in_table
    val styleTextAlignment = TextView.TEXT_ALIGNMENT_CENTER
    val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)

    for (i in getPercentAndMonth.indices) {

        // CREATE MONTH TABLE
        val monthTextView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            text = "${getPercentAndMonth[i].mounth} платежей"
            gravity = Gravity.CENTER
            setTextColor(styleTextColor)
            textSize = styleTextSize
            if (i < getPercentAndMonth.size - 1) {
                setBackgroundResource(styleBackground)
            }
            textAlignment = styleTextAlignment
            typeface = boldTypeface
        }
        monthTable.addView(monthTextView)

        //Расчет без первоначльного взноса
        val getMonth = getPercentAndMonth[i].mounth
        val getPercent = getPercentAndMonth[i].coefficient
        val getPercentFromTotalCart = (totalCart.toDouble() * getPercent) / 100
        val plusPercent = totalCart.toDouble() + getPercentFromTotalCart
        val tablePercent = plusPercent / getMonth

        // CREATE PERCENT TABLE
        val amountTextView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            firstPayCalculator.editText?.addTextChangedListener {
                val inputFirstPay = it.toString()
                if (inputFirstPay.isNotEmpty()) {
                    val getFirstPay = inputFirstPay

                    val realPercent = totalCart.toDouble() / 2
                    if (getFirstPay.toDouble() > realPercent) {
                        firstPayCalculator.error = "Первоначальный взнос должен составлять не менее 50% от суммы покупки!"
                        firstPay.text = "${formatNumber(0.00)} UZS"
                        text = formatNumber(tablePercent)
                        summa.text = "${formatNumber(totalCart)} UZS"
                    } else {
                        firstPayCalculator.error = null
                        val minusFirstPay = totalCart.toDouble() - getFirstPay.toDouble()
                        summa.text = "${formatNumber(minusFirstPay)} UZS"
                        val getPercentFromTotalCart = (minusFirstPay * getPercent.toInt()) / 100
                        val plusPercent = minusFirstPay + getPercentFromTotalCart
                        val tablePercentWithFirstPay = plusPercent / getMonth
                        text = formatNumber(tablePercentWithFirstPay)
                    }
                } else {
                    summa.text = "${formatNumber(totalCart)} UZS"
                    text = formatNumber(tablePercent)
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
                            val total = inputBonus.toDouble() + inputFirstPay.toDouble()
                            val totalWithOutBonusAndFirstPay = totalCart.toDouble() - total
                            val getPercentFromTotalCart = (totalWithOutBonusAndFirstPay * getPercent.toInt()) / 100
                            val plusPercent = totalWithOutBonusAndFirstPay + getPercentFromTotalCart
                            val tablePercentWithFirstPay = plusPercent / getMonth
                            text = formatNumber(tablePercentWithFirstPay)
                        }
                    }
                }
            }
            text = formatNumber(tablePercent)
            gravity = Gravity.CENTER
            setTextColor(styleTextColor)
            textSize = styleTextSize
            if (i < getPercentAndMonth.size - 1) {
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

    val totalCart = Cart.getTotalPrice()
    summa.text = "${formatNumber(totalCart)} UZS"

    val getTotalWithSale = Cart.getTotalPriceWithSale()
    tvSale.text = "${formatNumber(getTotalWithSale)} UZS"

    val allTypePay = listOf(
        AllTypePay(1, "Наличными"),
        AllTypePay(2, "В рассрочку")
    )

    val arrayAdapterTypeClient = ArrayAdapter(requireContext(), R.layout.spinner_item, getAllClient.map { it.client_name })
    spinnerClientType.setAdapter(arrayAdapterTypeClient)

    val arrayAdapterTypePay = ArrayAdapter(requireContext(), R.layout.spinner_item, allTypePay.map { it.name })
    typePay.setAdapter(arrayAdapterTypePay)

    spinnerClientType.setOnItemClickListener { _, _, position, _ ->
        val selectClientType = getAllClient[position]

        val contains = "розничный покупатель"
        val containsSubstring = selectClientType.client_name.contains(contains, true)

        if (containsSubstring){

            checkboxForBonus.visibility = View.GONE
            bonus.visibility = View.GONE

            checkBox.visibility = View.GONE
            firstPayCalculator.visibility = View.GONE

            tableSale.visibility = View.GONE
            tvTable.visibility = View.GONE

            val selectedClientBonus = selectClientType.bonus
            tvBonusForClient.text = "${formatNumber(selectedClientBonus)} UZS"

            typePay.setOnItemClickListener { _, _, position, _ ->
                val selectPayType = allTypePay[position]
                val selectPayTypeId = selectPayType.id

                if (selectPayTypeId == 1){

                    checkboxForBonus.visibility = View.GONE
                    checkboxForBonus.isChecked = false

                    bonus.visibility = View.GONE
                    bonus.editText?.setText("")

                    checkBox.visibility = View.GONE
                    checkBox.isChecked = false

                    firstPayCalculator.visibility = View.GONE
                    firstPayCalculator.editText?.setText("")

                    tableSale.visibility = View.GONE
                    tvTable.visibility = View.GONE

                    payWithBonus.text = "${formatNumber(0.00)} UZS"

                }else{

                    checkboxForBonus.visibility = View.GONE
                    checkboxForBonus.isChecked = false

                    bonus.visibility = View.GONE
                    bonus.editText?.setText("")

                    checkBox.visibility = View.VISIBLE
                    checkBox.isChecked = false

                    firstPayCalculator.editText?.setText("")

                    tableSale.visibility = View.VISIBLE
                    tvTable.visibility = View.VISIBLE

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked){
                            firstPayCalculator.visibility = View.VISIBLE
                            tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                            firstPayCalculator.editText?.addTextChangedListener {
                                val textFirstPayCalculator = it.toString()
                                if (textFirstPayCalculator.isNotEmpty()){
                                    val getTextFirstPayCalculator = textFirstPayCalculator.toDouble()
                                    val getTotalCart = totalCart.toDouble() / 2
                                    if (getTextFirstPayCalculator > getTotalCart){
                                        firstPayCalculator.error = "Первоначальный взнос должен составлять не менее 50% от суммы покупки!"
                                        firstPay.text = "${formatNumber(0.00)} UZS"
                                        summa.text = "${formatNumber(totalCart)} UZS"
                                    }else{
                                        firstPayCalculator.error = null
                                        tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                                        firstPay.text = "${formatNumber(getTextFirstPayCalculator)} UZS"
                                        val getTotal = totalCart.toDouble() - getTextFirstPayCalculator
                                        summa.text = "${formatNumber(getTotal)} UZS"
                                    }
                                }else{
                                    firstPay.text = "${formatNumber(0.00)} UZS"
                                    summa.text = "${formatNumber(totalCart)} UZS"
                                }
                            }
                        }else{
                            firstPayCalculator.error = null
                            firstPayCalculator.visibility = View.GONE
                            firstPayCalculator.editText?.setText("")
                            tvFirstPayCalculator.keyListener = null
                            firstPay.text = "${formatNumber(0.00)} UZS"
                            summa.text = "${formatNumber(totalCart)} UZS"
                        }
                    }
                }
            }
        }else{

            val selectedClientBonus = selectClientType.bonus
            tvBonusForClient.text = "${formatNumber(selectedClientBonus)} UZS"

            val getTotalWithSale = Cart.getTotalPriceWithSale()
            tvSale.text = "${formatNumber(getTotalWithSale)} UZS"

            checkboxForBonus.visibility = View.VISIBLE
            checkBox.visibility = View.GONE

            editBonus.inputType = InputType.TYPE_CLASS_NUMBER

            checkBox.visibility = View.GONE
            firstPayCalculator.visibility = View.GONE

            tableSale.visibility = View.GONE
            tvTable.visibility = View.GONE

            bonus.editText?.addTextChangedListener {
                val getText = it.toString()
                if (getText.isNotEmpty()){
                    val getTextBonus = getText.toDouble()
                    payWithBonus.text = "${formatNumber(getTextBonus)} UZS"
                }
            }

            typePay.setOnItemClickListener { _, _, position, _ ->
                val selectPayType = allTypePay[position]
                val selectPayTypeId = selectPayType.id

                if (selectPayTypeId == 1){
                    checkboxForBonus.visibility = View.VISIBLE
                    checkboxForBonus.isChecked = false

                    checkBox.visibility = View.GONE
                    firstPayCalculator.visibility = View.GONE

                    tableSale.visibility = View.GONE
                    tvTable.visibility = View.GONE

                    editBonus.inputType = InputType.TYPE_CLASS_NUMBER

                    firstPay.text = "${formatNumber(0.00)} UZS"
                    payWithBonus.text = "${formatNumber(0.00)} UZS"

                    summa.text = "${formatNumber(totalCart)} UZS"


                    checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked){
                            bonus.visibility = View.VISIBLE
                            bonus.editText?.addTextChangedListener {
                                val getText = it.toString()
                                if (getText.isNotEmpty()){
                                    val getTextBonus = getText.toDouble()
                                    val getTotalCart = totalCart.toDouble() - getTextBonus
                                    payWithBonus.text = "${formatNumber(getTextBonus)} UZS"
                                    summa.text = "${formatNumber(getTotalCart)} UZS"
                                }else{
                                    summa.text = "${formatNumber(totalCart)} UZS"
                                    payWithBonus.text = "${formatNumber(0.00)} UZS"
                                }
                            }
                        }else{
                            editBonus.keyListener = null
                            bonus.visibility = View.GONE
                            bonus.editText?.setText("")
                            summa.text = "${formatNumber(totalCart)} UZS"
                            payWithBonus.text = "${formatNumber(0.00)} UZS"
                        }
                    }
                }else{
                    checkBox.visibility = View.GONE
                    firstPayCalculator.visibility = View.GONE

                    checkboxForBonus.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked){
                            bonus.visibility = View.VISIBLE
                            editBonus.inputType = InputType.TYPE_CLASS_NUMBER

                            bonus.editText?.addTextChangedListener {
                                val getText = it.toString()
                                if (getText.isNotEmpty()){
                                    val getTextBonus = getText.toDouble()

                                    val getTotalCart = totalCart.toDouble() - getTextBonus
                                    summa.text = "${formatNumber(getTotalCart)} UZS"

                                    checkBox.visibility = View.VISIBLE
                                    checkBox.isChecked = false

                                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                                        if (isChecked){
                                            firstPayCalculator.visibility = View.VISIBLE
                                            tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                                            firstPayCalculator.editText?.addTextChangedListener {
                                                val getText = it.toString()
                                                if (getText.isNotEmpty()){
                                                    val getTextFirstPay = getText.toDouble()
                                                    val getTotalCart = totalCart.toDouble() - (getTextBonus + getTextFirstPay)
                                                    summa.text = "${formatNumber(getTotalCart)} UZS"
                                                    firstPay.text = "${formatNumber(getTextFirstPay)} UZS"
                                                    payWithBonus.text = "${formatNumber(getTextBonus)} UZS"
                                                }
                                            }
                                        }else{
                                            firstPayCalculator.visibility = View.GONE
                                            firstPayCalculator.editText?.setText("")
                                            tvFirstPayCalculator.keyListener = null
                                            firstPay.text = "${formatNumber(0.00)} UZS"
                                            val getTotalCart = totalCart.toDouble() - getTextBonus
                                            summa.text = "${formatNumber(getTotalCart)} UZS"

                                        }
                                    }
                                }
                            }
                            checkBox.visibility = View.VISIBLE
                            checkBox.isChecked = false


                        }else{
                            bonus.visibility = View.GONE
                            bonus.editText?.setText("")
                            editBonus.keyListener = null
                            checkBox.visibility = View.VISIBLE
                            firstPayCalculator.visibility = View.VISIBLE
                            checkBox.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked){
                                    firstPayCalculator.visibility = View.VISIBLE
                                    tvFirstPayCalculator.inputType = InputType.TYPE_CLASS_NUMBER
                                    firstPayCalculator.editText?.addTextChangedListener {
                                        val getText = it.toString()
                                        if (getText.isNotEmpty()){
                                            val getTextFirstPay = getText.toDouble()
                                            val getTotalCart = totalCart.toDouble() -  getTextFirstPay
                                            summa.text = "${formatNumber(getTotalCart)} UZS"
                                            firstPay.text = "${formatNumber(getTextFirstPay)} UZS"
                                            payWithBonus.text = "${formatNumber(0.00)} UZS"
                                        }
                                    }
                                }else{
                                    firstPayCalculator.visibility = View.GONE
                                    firstPayCalculator.editText?.setText("")
                                    tvFirstPayCalculator.keyListener = null
                                    payWithBonus.text = "${formatNumber(0.00)} UZS"
                                    summa.text = "${formatNumber(totalCart)} UZS"

                                }
                            }

                        }
                    }
                    tableSale.visibility = View.VISIBLE
                    tvTable.visibility = View.VISIBLE
                }
            }
        }
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
                Log.d("TAG", "onResponse: ${response.code()}")
                Log.d("TAG", "onResponse: ${response.errorBody()?.string()}")
                if (response.isSuccessful){
                    val percentAndMonth = response.body()
                    if (percentAndMonth != null){
                        getPercentAndMonth = percentAndMonth.result
                        val monthAndPercent = Gson().toJson(getAllClient)
                        prefs.edit().putString("percentAndMonth", monthAndPercent).apply()
                    }
                }else{
                    Log.d("TAG", "onResponse: ${response.code()}")
                    Log.d("TAG", "onResponse: ${response.errorBody()?.string()}")
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

fun CalculatorFragment.fetchClientFromApi(){
    try {
        val call = api.getApiService().getAllClients("Bearer ${sessionManager.fetchToken()}")
        call.enqueue(object : Callback<GetAllClientsResponse?> {
            override fun onResponse(
                call: Call<GetAllClientsResponse?>,
                response: Response<GetAllClientsResponse?>
            ) {
                if (response.isSuccessful){
                    val clientName = response.body()
                    if (clientName != null){
                        getAllClient = clientName.result
                        val nameClient = Gson().toJson(getAllClient)
                        prefs.edit().putString("clientName", nameClient).apply()
                        fetchClient(getAllClient)
                    }
                }else{
                    Log.d("TAG", "onResponse: ${response.code()}")
                    Log.d("TAG", "onResponse: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GetAllClientsResponse?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")                }
        })
    }catch (e: Exception){
        Log.d("TAG", "fetchClientNameFromApi: ${e.message}")
    }
}

fun CalculatorFragment.fetchClient(client: List<Client>){
    val arrayAdapterClient = ArrayAdapter(requireContext(), R.layout.spinner_item, client.map { it.client_name })
    spinnerClientType.setAdapter(arrayAdapterClient)

    spinnerClientType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {}

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

fun CalculatorFragment.fetchClientNameFromPrefs(){
    try {
        val clientNameJason = prefs.getString("clientName", null)
        if (clientNameJason != null){
            val clientNameListType = object : TypeToken<List<Client>>(){}.type
            val nameClient = Gson().fromJson<List<Client>>(clientNameJason,clientNameListType)
            getAllClient = nameClient
            Log.d("TAG", "fetchNameClientFromPrefs: $nameClient")
            fetchClient(getAllClient)
        }else{
            fetchClientFromApi()
        }
    }catch (e:Exception){
        Log.d("TAG", "fetchNameClientFromPrefs: ${e.message}")
    }
}
